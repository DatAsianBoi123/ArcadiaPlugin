package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.commands.arguments.ArcadiaArgumentType;
import com.datasiqn.arcadia.dungeons.DungeonInstance;
import com.datasiqn.arcadia.players.ArcadiaSender;
import com.datasiqn.commandcore.commands.builder.ArgumentBuilder;
import com.datasiqn.commandcore.commands.builder.CommandBuilder;
import com.datasiqn.commandcore.commands.builder.LiteralBuilder;

public class CommandDungeons {
    private final Arcadia plugin;

    public CommandDungeons(Arcadia plugin) {
        this.plugin = plugin;
    }

    public CommandBuilder getCommand() {
        return new CommandBuilder()
                .permission(ArcadiaPermission.PERMISSION_MANAGE_DUNGEONS)
                .description("Manages different dungeon instances")
                .then(LiteralBuilder.literal("create")
                        .executes(context -> {
                            ArcadiaSender<?> sender = context.getSource().getPlayer().matchResult(player -> plugin.getPlayerManager().getPlayerData(player).getSender(), err -> new ArcadiaSender<>(context.getSource().getSender()));
                            DungeonInstance instance = plugin.getDungeonManager().createDungeon();
                            if (instance == null) {
                                sender.sendError("An unexpected error occurred. Please try again later");
                                return;
                            }
                            sender.sendMessage("Successfully created a new dungeon with the id of " + instance.getId());
                        }))
                .then(LiteralBuilder.literal("delete")
                        .then(ArgumentBuilder.argument(ArcadiaArgumentType.DUNGEON, "world name")
                                .executes(context -> context.getArguments().get(1, ArcadiaArgumentType.DUNGEON).ifOk(instance -> {
                                    ArcadiaSender<?> sender = context.getSource().getPlayer().matchResult(player -> plugin.getPlayerManager().getPlayerData(player).getSender(), err -> new ArcadiaSender<>(context.getSource().getSender()));
                                    if (!plugin.getDungeonManager().deleteDungeon(instance)) {
                                        sender.sendError("An error occurred when deleting the world. Please try again later");
                                        return;
                                    }
                                    sender.sendMessage("Successfully deleted the dungeon " + instance.getId());
                                }))))
                .then(LiteralBuilder.literal("tp")
                        .then(ArgumentBuilder.argument(ArcadiaArgumentType.DUNGEON, "dungeon id")
                                .requiresPlayer()
                                .executes(context -> plugin.getDungeonManager().addPlayerTo(context.getSource().getPlayer().unwrap(), context.getArguments().get(1, ArcadiaArgumentType.DUNGEON).unwrap()))));
    }
}
