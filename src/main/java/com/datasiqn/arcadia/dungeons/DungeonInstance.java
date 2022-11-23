package com.datasiqn.arcadia.dungeons;

import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

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

    public void addPlayer(Player player) {

    }
}
