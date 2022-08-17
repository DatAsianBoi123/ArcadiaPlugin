package com.datasiqn.arcadia.managers;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.players.ArcadiaSender;
import com.datasiqn.arcadia.players.PlayerStats;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {
    private final Map<UUID, PlayerData> playerMap = new HashMap<>();
    private final Arcadia plugin;

    public PlayerManager(Arcadia plugin) {
        this.plugin = plugin;
    }

    public PlayerData getPlayerData(@NotNull ArcadiaSender<Player> player) {
        return getPlayerData(player.get());
    }
    public PlayerData getPlayerData(@NotNull UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return null;
        return getPlayerData(player);
    }
    public PlayerData getPlayerData(@NotNull Player player) {
        if (playerMap.containsKey(player.getUniqueId())) return playerMap.get(player.getUniqueId());
        ArcadiaSender<Player> sender = new ArcadiaSender<>(plugin, player);
        PlayerData playerData = new PlayerData(sender, PlayerStats.create(sender, plugin));
        playerMap.put(player.getUniqueId(), playerData);
        return playerData;
    }

    public void removePlayer(@NotNull Player player) {
        playerMap.remove(player.getUniqueId());
    }

    public record PlayerData(ArcadiaSender<Player> player, PlayerStats playerStats) {}
}
