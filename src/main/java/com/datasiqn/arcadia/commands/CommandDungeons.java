package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.commands.argument.ArcadiaArgumentType;
import com.datasiqn.arcadia.dungeon.DungeonInstance;
import com.datasiqn.arcadia.dungeon.DungeonPlayer;
import com.datasiqn.arcadia.player.ArcadiaSender;
import com.datasiqn.arcadia.upgrade.UpgradeType;
import com.datasiqn.commandcore.argument.type.ArgumentType;
import com.datasiqn.commandcore.command.CommandContext;
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
                .executes((context, source, arguments) -> {
                    Player player = source.getPlayer();
                    DungeonInstance dungeon = plugin.getDungeonManager().getJoinedDungeon(player);
                    if (dungeon == null) {
                        player.sendMessage("You are not in a dungeon currently");
                        return;
                    }
                    player.sendMessage("You are in the dungeon " + dungeon.getId() + " with " + dungeon.getPlayers() + " other people");
                })
                .then(LiteralBuilder.literal("create")
                        .executes((context, source, arguments) -> {
                            ArcadiaSender<?> sender = source.getPlayerChecked().matchResult(player -> plugin.getPlayerManager().getPlayerData(player).getSender(), err -> new ArcadiaSender<>(source.sender()));
                            DungeonInstance instance = plugin.getDungeonManager().createDungeon();
                            if (instance == null) {
                                sender.sendError("An unexpected error occurred. Please try again later");
                                return;
                            }
                            sender.sendMessage("Successfully created a new dungeon with the id of " + instance.getId());
                        }))
                .then(LiteralBuilder.literal("delete")
                        .then(ArgumentBuilder.argument(ArcadiaArgumentType.DUNGEON, "world name")
                                .executes((context, source, arguments) -> {
                                    DungeonInstance instance = arguments.get(1, ArcadiaArgumentType.DUNGEON);
                                    ArcadiaSender<?> sender = source.getPlayerChecked().matchResult(player -> plugin.getPlayerManager().getPlayerData(player).getSender(), err -> new ArcadiaSender<>(source.sender()));
                                    if (!plugin.getDungeonManager().deleteDungeon(instance)) {
                                        sender.sendError("An error occurred when deleting the world. Please try again later");
                                        return;
                                    }
                                    sender.sendMessage("Successfully deleted the dungeon " + instance.getId());
                                })))
                .then(LiteralBuilder.literal("tp")
                        .then(ArgumentBuilder.argument(ArcadiaArgumentType.DUNGEON, "dungeon id")
                                .requiresPlayer()
                                .executes((context, source, arguments) -> plugin.getDungeonManager().addPlayerTo(plugin.getPlayerManager().getPlayerData(source.getPlayer()), arguments.get(1, ArcadiaArgumentType.DUNGEON)))))
                .then(LiteralBuilder.literal("pickup")
                        .then(ArgumentBuilder.argument(ArcadiaArgumentType.UPGRADE, "upgrade")
                                .then(ArgumentBuilder.argument(ArgumentType.rangedNumber(int.class, 1), "amount")
                                        .requiresPlayer()
                                        .executes((context, source, arguments) -> pickupItem(context, arguments.get(1, ArcadiaArgumentType.UPGRADE), arguments.get(2, ArgumentType.rangedNumber(int.class, 1)))))
                                .requiresPlayer()
                                .executes((context, source, arguments) -> pickupItem(context, arguments.get(1, ArcadiaArgumentType.UPGRADE), 1)))
                        .then(ArgumentBuilder.argument(ArgumentType.rangedNumber(int.class, 1), "amount")
                                .requiresPlayer()
                                .executes((context, source, arguments) -> {
                                    int amount = arguments.get(1, ArgumentType.rangedNumber(int.class, 1));
                                    for (int i = 0; i < amount; i++) {
                                        pickupItem(context, UpgradeType.getRandomWeighted(), 1);
                                    }
                                }))
                        .requiresPlayer()
                        .executes((context, source, arguments) -> pickupItem(context, UpgradeType.getRandomWeighted(), 1)));
    }

    private void pickupItem(@NotNull CommandContext context, UpgradeType upgrade, int amount) {
        Player player = context.source().getPlayer();
        DungeonPlayer dungeonPlayer = plugin.getDungeonManager().getDungeonPlayer(player);
        ArcadiaSender<Player> sender = plugin.getPlayerManager().getPlayerData(player).getSender();
        if (dungeonPlayer == null) {
            sender.sendError("You are not in a dungeon");
            return;
        }
        dungeonPlayer.pickupUpgrade(upgrade, amount);
        sender.sendMessage("Picked up item " + upgrade + "x" + amount);
    }
}
