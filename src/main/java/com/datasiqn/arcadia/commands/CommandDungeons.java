package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.commands.argument.ArcadiaArgumentType;
import com.datasiqn.arcadia.dungeon.DungeonInstance;
import com.datasiqn.arcadia.dungeon.DungeonPlayer;
import com.datasiqn.arcadia.player.ArcadiaSender;
import com.datasiqn.arcadia.upgrade.UpgradeType;
import com.datasiqn.commandcore.command.CommandContext;
import com.datasiqn.commandcore.command.CommandSource;
import com.datasiqn.commandcore.command.builder.ArgumentBuilder;
import com.datasiqn.commandcore.command.builder.CommandBuilder;
import com.datasiqn.commandcore.command.builder.LiteralBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandDungeons {
    private final Arcadia plugin;

    public CommandDungeons(Arcadia plugin) {
        this.plugin = plugin;
    }

    public CommandBuilder getCommand() {
        return new CommandBuilder("dungeons")
                .permission(ArcadiaPermission.PERMISSION_MANAGE_DUNGEONS)
                .description("Manages different dungeon instances")
                .requiresPlayer()
                .executes(context -> {
                    Player player = context.getSource().getPlayer();
                    DungeonInstance dungeon = plugin.getDungeonManager().getJoinedDungeon(player);
                    if (dungeon == null) {
                        player.sendMessage("You are not in a dungeon currently");
                        return;
                    }
                    player.sendMessage("You are in the dungeon " + dungeon.getId() + " with " + dungeon.getPlayers() + " other people");
                })
                .then(LiteralBuilder.literal("create")
                        .executes(context -> {
                            CommandSource source = context.getSource();
                            ArcadiaSender<?> sender = source.getPlayerChecked().matchResult(player -> plugin.getPlayerManager().getPlayerData(player).getSender(), err -> new ArcadiaSender<>(source.getSender()));
                            DungeonInstance instance = plugin.getDungeonManager().createDungeon();
                            if (instance == null) {
                                sender.sendError("An unexpected error occurred. Please try again later");
                                return;
                            }
                            sender.sendMessage("Successfully created a new dungeon with the id of " + instance.getId());
                        }))
                .then(LiteralBuilder.literal("delete")
                        .then(ArgumentBuilder.argument(ArcadiaArgumentType.DUNGEON, "world name")
                                .executes(context -> {
                                    DungeonInstance instance = context.getArguments().get(1, ArcadiaArgumentType.DUNGEON);
                                    ArcadiaSender<?> sender = context.getSource().getPlayerChecked().matchResult(player -> plugin.getPlayerManager().getPlayerData(player).getSender(), err -> new ArcadiaSender<>(context.getSource().getSender()));
                                    if (!plugin.getDungeonManager().deleteDungeon(instance)) {
                                        sender.sendError("An error occurred when deleting the world. Please try again later");
                                        return;
                                    }
                                    sender.sendMessage("Successfully deleted the dungeon " + instance.getId());
                                })))
                .then(LiteralBuilder.literal("tp")
                        .then(ArgumentBuilder.argument(ArcadiaArgumentType.DUNGEON, "dungeon id")
                                .requiresPlayer()
                                .executes(context -> plugin.getDungeonManager().addPlayerTo(plugin.getPlayerManager().getPlayerData(context.getSource().getPlayer()), context.getArguments().get(1, ArcadiaArgumentType.DUNGEON)))))
                .then(LiteralBuilder.literal("pickup")
                        .then(ArgumentBuilder.argument(ArcadiaArgumentType.UPGRADE, "upgrade")
                                .requiresPlayer()
                                .executes(context -> pickupItem(context, context.getArguments().get(1, ArcadiaArgumentType.UPGRADE))))
                        .requiresPlayer()
                        .executes(context -> pickupItem(context, UpgradeType.getRandomWeighted())));
    }

    private void pickupItem(@NotNull CommandContext context, UpgradeType upgrade) {
        Player player = context.getSource().getPlayer();
        DungeonPlayer dungeonPlayer = plugin.getDungeonManager().getDungeonPlayer(player.getUniqueId());
        if (dungeonPlayer == null) {
            plugin.getPlayerManager().getPlayerData(player).getSender().sendError("You are not in a dungeon");
            return;
        }
        dungeonPlayer.pickupUpgrade(upgrade);
    }
}
