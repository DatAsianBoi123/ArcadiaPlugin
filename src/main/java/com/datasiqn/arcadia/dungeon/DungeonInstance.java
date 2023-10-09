package com.datasiqn.arcadia.dungeon;

import com.datasiqn.arcadia.player.PlayerData;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class DungeonInstance {
    private final Set<DungeonPlayer> players = new HashSet<>();
    private final World world;
    private final String id;

    public DungeonInstance(World world, String id) {
        this.world = world;
        this.id = id;
    }

    public World getWorld() {
        return world;
    }

    public String getId() {
        return id;
    }

    public boolean hasPlayer(@NotNull DungeonPlayer dungeonPlayer) {
        return players.contains(dungeonPlayer);
    }

    public DungeonPlayer getPlayer(@NotNull PlayerData playerData) {
        return getPlayer(playerData.getUniqueId());
    }
    public DungeonPlayer getPlayer(@NotNull Player player) {
        return getPlayer(player.getUniqueId());
    }
    public DungeonPlayer getPlayer(@Nullable UUID uuid) {
        return players.stream().filter(dungeonPlayer -> dungeonPlayer.getUniqueId().equals(uuid)).findFirst().orElse(null);
    }

    public @NotNull DungeonPlayer addPlayer(PlayerData player) {
        DungeonPlayer dungeonPlayer = new DungeonPlayer(player);
        if (hasPlayer(dungeonPlayer)) return getPlayer(player);
        players.add(dungeonPlayer);
        return dungeonPlayer;
    }

    public void removePlayer(@NotNull DungeonPlayer dungeonPlayer) {
        players.remove(dungeonPlayer);
    }

    @Contract(pure = true)
    @UnmodifiableView
    public @NotNull Set<DungeonPlayer> getJoinedPlayers() {
        return Collections.unmodifiableSet(players);
    }

    public int getPlayers() {
        return players.size();
    }
}
