package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.commands.argument.ArcadiaArgumentType;
import com.datasiqn.arcadia.managers.PlayerManager;
import com.datasiqn.arcadia.player.PlayerData;
import com.datasiqn.commandcore.argument.type.ArgumentType;
import com.datasiqn.commandcore.command.builder.ArgumentBuilder;
import com.datasiqn.commandcore.command.builder.CommandBuilder;
import com.datasiqn.commandcore.command.builder.LiteralBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommandPlayerData {
    private static final DecimalFormat ATTRIBUTE_FORMAT = new DecimalFormat("#.##");

    private final Arcadia plugin;
    private final PlayerManager playerManager;

    public CommandPlayerData(@NotNull Arcadia plugin) {
        this.plugin = plugin;
        this.playerManager = plugin.getPlayerManager();
    }

    public CommandBuilder getCommand() {
        return new CommandBuilder("playerdata")
                .permission(ArcadiaPermission.PERMISSION_MANAGE_DATA)
                .description("Manages player data")
                .then(LiteralBuilder.literal("load")
                        .then(ArgumentBuilder.argument(ArcadiaArgumentType.PLAYER, "player")
                                .executesAsync((context, source, arguments) -> {
                                    PlayerData playerData = arguments.get(1, ArcadiaArgumentType.PLAYER);
                                    playerData.loadData();
                                    playerData.getSender().sendMessage("Successfully loaded " + playerData.getPlayer().getName() + "'s player data");
                                }))
                        .executes((context, source, arguments) -> {
                            ExecutorService threadPool = Executors.newFixedThreadPool(4);
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                threadPool.submit(() -> {
                                    playerManager.getPlayerData(player).loadData();
                                    playerManager.getPlayerData(source.getPlayer()).getSender().sendMessage("Successfully loaded " + player.getName() + "'s data");
                                });
                            }
                        }))
                .then(LiteralBuilder.literal("save")
                        .then(ArgumentBuilder.argument(ArcadiaArgumentType.PLAYER, "player")
                                .executesAsync((context, source, arguments) -> {
                                    PlayerData playerData = arguments.get(1, ArcadiaArgumentType.PLAYER);
                                    playerData.saveData();
                                    playerData.getSender().sendMessage("Successfully saved " + playerData.getPlayer().getName() + "'s player data");
                                }))
                        .executes((context, source, arguments) -> {
                            ExecutorService threadPool = Executors.newFixedThreadPool(4);
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                threadPool.submit(() -> {
                                    playerManager.getPlayerData(player).saveData();
                                    playerManager.getPlayerData(source.getPlayer()).getSender().sendMessage("Successfully saved " + player.getName() + "'s data");
                                });
                            }
                        }))
                .then(LiteralBuilder.literal("attribute")
                        .then(LiteralBuilder.literal("get")
                                .then(ArgumentBuilder.argument(ArcadiaArgumentType.PLAYER_ATTRIBUTE, "attribute")
                                        .requiresPlayer()
                                        .executes((context, source, arguments) -> {
                                            PlayerData playerData = playerManager.getPlayerData(source.getPlayer());
                                            playerData.getSender().sendMessage(ATTRIBUTE_FORMAT.format(playerData.getAttribute(arguments.get(2, ArcadiaArgumentType.PLAYER_ATTRIBUTE))));
                                        }))))
                .then(LiteralBuilder.literal("xp")
                        .then(LiteralBuilder.literal("set")
                                .then(ArgumentBuilder.argument(ArgumentType.boundedNumber(long.class, 0L), "amount")
                                        .requiresPlayer()
                                        .executes((context, source, arguments) -> {
                                            Player player = source.getPlayer();
                                            PlayerData playerData = playerManager.getPlayerData(player);
                                            playerData.getXp().setAmount(arguments.get(2, ArgumentType.number(long.class)));
                                            plugin.getScoreboardManager().updateScoreboard(player);
                                        }))));
    }
}
