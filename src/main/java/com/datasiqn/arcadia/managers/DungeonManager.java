package com.datasiqn.arcadia.managers;

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
import java.util.concurrent.ConcurrentHashMap;

public class DungeonManager {
    public static final String DUNGEON_WORLD_PREFIX = "d_";

    private final Map<UUID, DungeonInstance> activeDungeonInstances = new HashMap<>();
    private final Set<DungeonInstance> createdDungeons = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public DungeonManager() {
    }

    public void loadDungeonsFromDisk() {
        Bukkit.getWorlds().stream().filter(world -> world.getName().startsWith(DUNGEON_WORLD_PREFIX)).forEach(world -> createdDungeons.add(new DungeonInstance(world, world.getName().substring(DUNGEON_WORLD_PREFIX.length()))));
    }

    public @Nullable DungeonInstance createDungeon() {
        String id = RandomStringUtils.randomNumeric(5);
        while (Bukkit.getWorld(DUNGEON_WORLD_PREFIX + id) != null) {
            id = RandomStringUtils.randomNumeric(5);
        }
        Bukkit.getLogger().info("Started async future");
        World world = Bukkit.createWorld(WorldCreator.name(DUNGEON_WORLD_PREFIX + id).type(WorldType.FLAT).generateStructures(false));
        Bukkit.getLogger().info("Created world");
        if (world == null) return null;
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setSpawnFlags(false, false);
        DungeonInstance instance = new DungeonInstance(world, id);
        Bukkit.getLogger().info("Instantiated new dungeon instance");
        createdDungeons.add(instance);
        Bukkit.getLogger().info("Added to created dungeons set");
        return instance;
    }

    public boolean deleteDungeon(String id) {
        DungeonInstance createdDungeon = getCreatedDungeon(id);
        if (createdDungeon == null) return false;
        return deleteDungeon(createdDungeon);
    }
    public boolean deleteDungeon(@NotNull DungeonInstance instance) {
        World world = instance.world();
        if (!Bukkit.unloadWorld(world, false)) return false;
        try {
            FileUtils.deleteDirectory(world.getWorldFolder());
        } catch (IOException e) {
            return false;
        }
        createdDungeons.remove(instance);
        return true;
    }

    public void joinDungeon(@NotNull Player player, @NotNull String dungeonId) {
        DungeonInstance instance = getCreatedDungeon(dungeonId);
        if (instance == null) {
            World world = Bukkit.getWorld(DUNGEON_WORLD_PREFIX + dungeonId);
            if (world != null) {
                instance = new DungeonInstance(world, dungeonId);
            }
        }
        if (instance == null) return;
        joinDungeon(player, instance);
    }
    public void joinDungeon(@NotNull Player player, @NotNull DungeonInstance instance) {
        player.teleport(instance.world().getSpawnLocation());
        activeDungeonInstances.put(player.getUniqueId(), instance);
    }

    public void leaveDungeon(@NotNull Player player) {
        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        activeDungeonInstances.remove(player.getUniqueId());
    }

    public @Nullable DungeonInstance getJoinedDungeon(@NotNull Player player) {
        return getJoinedDungeon(player.getUniqueId());
    }
    public @Nullable DungeonInstance getJoinedDungeon(@NotNull UUID uuid) {
        return activeDungeonInstances.get(uuid);
    }

    public @Nullable DungeonInstance getCreatedDungeon(String id) {
        return createdDungeons.stream().filter(instance -> instance.id().equals(id)).findAny().orElse(null);
    }

    @NotNull
    @Unmodifiable
    public Set<DungeonInstance> getAllDungeonInstances() {
        return Set.copyOf(createdDungeons);
    }
}
