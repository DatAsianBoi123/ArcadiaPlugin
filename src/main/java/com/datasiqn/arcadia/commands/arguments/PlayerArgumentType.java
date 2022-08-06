package com.datasiqn.arcadia.commands.arguments;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.managers.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlayerArgumentType implements ArgumentType<PlayerManager.PlayerData> {
    private final Arcadia plugin;

    public PlayerArgumentType(Arcadia plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull Optional<PlayerManager.PlayerData> fromString(@NotNull String str) {
        Player player = Bukkit.getPlayer(str);
        if (player == null) return Optional.empty();
        return Optional.of(plugin.getPlayerManager().getPlayerData(player));
    }

    @Override
    public @NotNull List<String> all() {
        ArrayList<Player> playerList = new ArrayList<>(Bukkit.getOnlinePlayers());
        return playerList.stream().map(Player::getName).toList();
    }
}
