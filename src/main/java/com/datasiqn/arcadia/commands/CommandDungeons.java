package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.commands.arguments.ArcadiaArgumentType;
import com.datasiqn.arcadia.dungeons.DungeonInstance;
import com.datasiqn.arcadia.players.ArcadiaSender;
import com.datasiqn.commandcore.commands.Command;
import com.datasiqn.commandcore.commands.builder.ArgumentBuilder;
import com.datasiqn.commandcore.commands.builder.CommandBuilder;
import com.datasiqn.commandcore.commands.builder.LiteralBuilder;
import org.bukkit.entity.Player;

public class CommandDungeons {
    private final Arcadia plugin;

    public CommandDungeons(Arcadia plugin) {
        this.plugin = plugin;
    }

    public Command getCommand() {
        return new CommandBuilder<>(Player.class)
                .permission(ArcadiaPermission.PERMISSION_MANAGE_DUNGEONS)
                .description("Manages different dungeon instances")
                .then(LiteralBuilder.<Player>literal("create")
                        .executes(context -> {
                            ArcadiaSender<Player> player = plugin.getPlayerManager().getPlayerData(context.getSender()).getPlayer();
                            DungeonInstance instance = plugin.getDungeonManager().createDungeon();
                            if (instance == null) {
                                player.sendError("An unexpected error occurred. Please try again later");
                                return;
                            }
                            player.sendMessage("Successfully created a new dungeon with the id of " + instance.id());
                        }))
                .then(LiteralBuilder.<Player>literal("delete")
                        .then(ArgumentBuilder.<Player, DungeonInstance>argument(ArcadiaArgumentType.DUNGEON, "world name")
                                .executes(context -> {
                                    DungeonInstance instance = context.parseArgument(ArcadiaArgumentType.DUNGEON, 1);
                                    ArcadiaSender<Player> player = plugin.getPlayerManager().getPlayerData(context.getSender()).getPlayer();
                                    if (!plugin.getDungeonManager().deleteDungeon(instance)) {
                                        player.sendError("An error occurred when deleting the world. Please try again later");
                                        return;
                                    }
                                    player.sendMessage("Successfully deleted the dungeon " + instance.id());
                                })))
                .then(LiteralBuilder.<Player>literal("tp")
                        .then(ArgumentBuilder.<Player, DungeonInstance>argument(ArcadiaArgumentType.DUNGEON, "dungeon id")
                                .executes(context -> plugin.getDungeonManager().joinDungeon(context.getSender(), context.parseArgument(ArcadiaArgumentType.DUNGEON, 1)))))
                .build();
    }
}
