package com.datasiqn.arcadia.managers;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.dungeon.DungeonInstance;
import com.datasiqn.arcadia.dungeon.DungeonPlayer;
import com.datasiqn.arcadia.player.PlayerData;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.IOException;
import java.util.*;

public class DungeonManager {
    public static final String DEFAULT_DUNGEON_NAME = "world_default_dungeon";
    public static final String DUNGEON_WORLD_PREFIX = "d_";

    private final Set<DungeonInstance> dungeonInstances = new HashSet<>();
    private final Map<DungeonPlayer, DungeonInstance> playerToDungeonMap = new HashMap<>();
    private final Arcadia plugin;

    public DungeonManager(Arcadia plugin) {
        this.plugin = plugin;
    }

    public void loadDungeonsFromDisk() {
        for (World world : Bukkit.getWorlds()) {
            if (world.getName().startsWith(DUNGEON_WORLD_PREFIX)) {
                dungeonInstances.add(new DungeonInstance(world, world.getName().substring(DUNGEON_WORLD_PREFIX.length())));
            }
        }
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
        dungeonInstances.add(instance);
        plugin.getLogger().info("Created new dungeon with the id of " + id + ". Creation took " + (after - before) + "ms");
        return instance;
    }

    public boolean deleteDungeon(@NotNull DungeonInstance instance) {
        World world = instance.getWorld();
        if (!Bukkit.unloadWorld(world, false)) return false;
        try {
            FileUtils.deleteDirectory(world.getWorldFolder());
        } catch (IOException e) {
            return false;
        }
        dungeonInstances.remove(instance);
        plugin.getLogger().info("Deleted dungeon " + instance.getId());
        return true;
    }

    public void addPlayerTo(@NotNull PlayerData playerData, @NotNull DungeonInstance instance) {
        if (getJoinedDungeon(playerData.getUniqueId()) != null) return;
        DungeonPlayer dungeonPlayer = instance.addPlayer(playerData);
        playerToDungeonMap.put(dungeonPlayer, instance);
        playerData.getPlayer().teleport(instance.getWorld().getSpawnLocation());
        plugin.getLogger().info("Player " + playerData.getPlayer().getName() + " (" + playerData.getUniqueId() + ") just joined the dungeon " + instance.getId());
    }

    public void leaveDungeon(@NotNull Player player) {
        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        DungeonInstance instance = getJoinedDungeon(player);
        if (instance == null) return;
        DungeonPlayer dungeonPlayer = instance.getPlayer(player);
        playerToDungeonMap.remove(dungeonPlayer);
        instance.removePlayer(dungeonPlayer);
        plugin.getLogger().info("Player " + player.getName() + " (" + player.getUniqueId() + ") just left the dungeon they were in (" + instance.getId());
    }

    public @Nullable DungeonInstance getJoinedDungeon(@NotNull Player player) {
        return getJoinedDungeon(player.getUniqueId());
    }
    public @Nullable DungeonInstance getJoinedDungeon(@NotNull UUID uuid) {
        for (Map.Entry<DungeonPlayer, DungeonInstance> entry : playerToDungeonMap.entrySet()) {
            if (entry.getKey().getUniqueId().equals(uuid)) return entry.getValue();
        }
        return null;
    }

    public @Nullable DungeonPlayer getDungeonPlayer(@NotNull UUID uuid) {
        DungeonInstance instance = getJoinedDungeon(uuid);
        if (instance == null) return null;
        return instance.getPlayer(uuid);
    }

    public @Nullable DungeonInstance getCreatedDungeon(String id) {
        Optional<DungeonInstance> dungeonInstance = dungeonInstances.stream().filter(instance -> instance.getId().equals(id)).findAny();
        if (dungeonInstance.isEmpty()) {
            World world = Bukkit.getWorld(DUNGEON_WORLD_PREFIX + id);
            if (world == null) return null;
            DungeonInstance instance = new DungeonInstance(world, id);
            dungeonInstances.add(instance);
            return instance;
        }
        return dungeonInstance.get();
    }

    @NotNull
    @Unmodifiable
    public Set<DungeonInstance> getAllDungeonInstances() {
        return Set.copyOf(dungeonInstances);
    }
}
