package com.datasiqn.arcadia.managers;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.dungeons.DungeonInstance;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.IOException;
import java.util.*;

public class DungeonManager {
    public static final String DEFAULT_DUNGEON_NAME = "world_default_dungeon";
    public static final String DUNGEON_WORLD_PREFIX = "d_";

    private final Map<UUID, DungeonInstance> activeDungeonInstances = new HashMap<>();
    private final Set<DungeonInstance> createdDungeons = new HashSet<>();
    private final Arcadia plugin;

    public DungeonManager(Arcadia plugin) {
        this.plugin = plugin;
    }

    public void loadDungeonsFromDisk() {
        Bukkit.getWorlds().stream().filter(world -> world.getName().startsWith(DUNGEON_WORLD_PREFIX)).forEach(world -> createdDungeons.add(new DungeonInstance(world, world.getName().substring(DUNGEON_WORLD_PREFIX.length()))));
    }

    public @Nullable DungeonInstance createDungeon() {
        String id = RandomStringUtils.randomNumeric(5);
        while (Bukkit.getWorld(DUNGEON_WORLD_PREFIX + id) != null) {
            id = RandomStringUtils.randomNumeric(5);
        }
        long before = System.currentTimeMillis();
        World defaultDungeon = Bukkit.getWorld(DEFAULT_DUNGEON_NAME);
        if (defaultDungeon == null) {
            plugin.getLogger().severe("Default world does not exist!");
            return null;
        }
        World world = WorldCreator.name(DUNGEON_WORLD_PREFIX + id).copy(defaultDungeon).createWorld();
        if (world == null) return null;
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setSpawnFlags(false, false);
        long after = System.currentTimeMillis();
        DungeonInstance instance = new DungeonInstance(world, id);
        createdDungeons.add(instance);
        plugin.getLogger().info("Created new dungeon with the id of " + id + ". Creation took " + (after - before) + "ms");
        return instance;
    }

    public boolean deleteDungeon(String id) {
        DungeonInstance createdDungeon = getCreatedDungeon(id);
        if (createdDungeon == null) return false;
        return deleteDungeon(createdDungeon);
    }
    public boolean deleteDungeon(@NotNull DungeonInstance instance) {
        World world = instance.getWorld();
        if (!Bukkit.unloadWorld(world, false)) return false;
        try {
            FileUtils.deleteDirectory(world.getWorldFolder());
        } catch (IOException e) {
            return false;
        }
        createdDungeons.remove(instance);
        plugin.getLogger().info("Deleted dungeon " + instance.getId());
        return true;
    }

    public void addPlayerTo(@NotNull Player player, @NotNull String dungeonId) {
        DungeonInstance instance = getCreatedDungeon(dungeonId);
        if (instance == null) return;
        addPlayerTo(player, instance);
    }
    public void addPlayerTo(@NotNull Player player, @NotNull DungeonInstance instance) {
        player.teleport(instance.getWorld().getSpawnLocation());
        activeDungeonInstances.put(player.getUniqueId(), instance);
        plugin.getLogger().info("Player " + player.getName() + " (" + player.getUniqueId() + ") just joined the dungeon " + instance.getId());
    }

    public void leaveDungeon(@NotNull Player player) {
        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        DungeonInstance instance = activeDungeonInstances.remove(player.getUniqueId());
        plugin.getLogger().info("Player " + player.getName() + " (" + player.getUniqueId() + ") just left the dungeon they were in (" + instance.getId());
    }

    public @Nullable DungeonInstance getJoinedDungeon(@NotNull Player player) {
        return getJoinedDungeon(player.getUniqueId());
    }
    public @Nullable DungeonInstance getJoinedDungeon(@NotNull UUID uuid) {
        return activeDungeonInstances.get(uuid);
    }

    public @Nullable DungeonInstance getCreatedDungeon(String id) {
        return createdDungeons.stream().filter(instance -> instance.getId().equals(id)).findAny().orElse(null);
    }

    @NotNull
    @Unmodifiable
    public Set<DungeonInstance> getAllDungeonInstances() {
        return Set.copyOf(createdDungeons);
    }
}
