package com.datasiqn.arcadia.managers;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.players.ArcadiaSender;
import com.datasiqn.arcadia.players.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {
    private final Map<UUID, PlayerData> playerMap = new HashMap<>();
    private final File dataFolder;
    private final Arcadia plugin;

    public PlayerManager(@NotNull Arcadia plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder().getPath() + File.separatorChar + "player-data");
    }

    public PlayerData getPlayerData(@NotNull ArcadiaSender<Player> player) {
        return getPlayerData(player.get());
    }
    public PlayerData getPlayerData(@NotNull UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return null;
        return getPlayerData(player);
    }
    @Contract("null -> null; !null -> !null")
    public PlayerData getPlayerData(@Nullable Player player) {
        if (player == null) return null;
        if (playerMap.containsKey(player.getUniqueId())) return playerMap.get(player.getUniqueId());
        ArcadiaSender<Player> sender = new ArcadiaSender<>(player);
        PlayerData playerData = PlayerData.create(sender, plugin);
        playerMap.put(player.getUniqueId(), playerData);
        return playerData;
    }

    public void removePlayer(@NotNull Player player) {
        playerMap.remove(player.getUniqueId());
    }

    public File getDataFolder() {
        return dataFolder;
    }
}
