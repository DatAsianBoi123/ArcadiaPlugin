package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.commands.argument.ArcadiaArgumentType;
import com.datasiqn.arcadia.managers.PlayerManager;
import com.datasiqn.arcadia.player.PlayerData;
import com.datasiqn.commandcore.command.builder.ArgumentBuilder;
import com.datasiqn.commandcore.command.builder.CommandBuilder;
import com.datasiqn.commandcore.command.builder.LiteralBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommandPlayerData {
    private final PlayerManager playerManager;

    @Contract(pure = true)
    public CommandPlayerData(@NotNull Arcadia plugin) {
        this.playerManager = plugin.getPlayerManager();
    }

    public CommandBuilder getCommand() {
        return new CommandBuilder("playerdata")
                .permission(ArcadiaPermission.PERMISSION_MANAGE_DATA)
                .description("Manages player data")
                .then(LiteralBuilder.literal("load")
                        .then(ArgumentBuilder.argument(ArcadiaArgumentType.PLAYER, "player")
                                .executes(context -> new Thread(() -> {
                                    PlayerData playerData = context.getArguments().get(1, ArcadiaArgumentType.PLAYER);
                                    playerData.loadData();
                                    playerData.getSender().sendMessage("Successfully loaded " + playerData.getPlayer().getName() + "'s player data");
                                }).start()))
                        .executes(context -> {
                            ExecutorService threadPool = Executors.newFixedThreadPool(4);
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                threadPool.submit(() -> {
                                    playerManager.getPlayerData(player).loadData();
                                    playerManager.getPlayerData(context.getSource().getPlayer()).getSender().sendMessage("Successfully loaded " + player.getName() + "'s data");
                                });
                            }
                        }))
                .then(LiteralBuilder.literal("save")
                        .then(ArgumentBuilder.argument(ArcadiaArgumentType.PLAYER, "player")
                                .executes(context -> new Thread(() -> {
                                    PlayerData playerData = context.getArguments().get(1, ArcadiaArgumentType.PLAYER);
                                    playerData.saveData();
                                    playerData.getSender().sendMessage("Successfully saved " + playerData.getPlayer().getName() + "'s player data");
                                }).start()))
                        .executes(context -> {
                            ExecutorService threadPool = Executors.newFixedThreadPool(4);
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                threadPool.submit(() -> {
                                    playerManager.getPlayerData(player).saveData();
                                    playerManager.getPlayerData(context.getSource().getPlayer()).getSender().sendMessage("Successfully saved " + player.getName() + "'s data");
                                });
                            }
                        }));
    }
}
