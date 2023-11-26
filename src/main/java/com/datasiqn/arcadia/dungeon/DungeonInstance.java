package com.datasiqn.arcadia.dungeon;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaTag;
import com.datasiqn.arcadia.player.PlayerData;
import com.datasiqn.arcadia.upgrade.UpgradeType;
import com.datasiqn.arcadia.upgrade.actions.GenerateUpgradeAction;
import com.datasiqn.arcadia.util.PdcUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public final class DungeonInstance {
    private final Set<DungeonPlayer> players = new HashSet<>();
    private final World world;
    private final String id;
    private final Arcadia plugin;

    public DungeonInstance(World world, String id, Arcadia plugin) {
        this.world = world;
        this.id = id;
        this.plugin = plugin;
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
        DungeonPlayer dungeonPlayer = new DungeonPlayer(player, this);
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

    public @NotNull Item dropUpgrade(Location location, @NotNull UpgradeType type) {
        return dropUpgrade(location, type, item -> {});
    }
    public @NotNull Item dropUpgrade(Location location, @NotNull UpgradeType type, Consumer<Item> consumer) {
        ItemStack upgrade = type.getData().toItemStack();
        return world.dropItem(location, upgrade, item -> {
            item.setUnlimitedLifetime(true);
            item.setGlowing(true);
            item.setCustomNameVisible(true);
            item.setVelocity(new Vector());
            ItemMeta meta = upgrade.getItemMeta();
            if (meta != null) item.setCustomName(meta.getDisplayName());

            for (DungeonPlayer player : players) {
                Team rarityTeam = type.getData().getRarity().getTeam(player.getPlayer().getScoreboard());
                if (rarityTeam == null) continue;
                rarityTeam.addEntry(item.getUniqueId().toString());
            }

            PdcUtil.set(item.getPersistentDataContainer(), ArcadiaTag.UPGRADE_TYPE, type);

            consumer.accept(item);
        });
    }

    public @NotNull Item generateUpgrade(Location location, DungeonPlayer generator) {
        return generateUpgrade(location, UpgradeType.getRandomWeighted(), generator);
    }
    public @NotNull Item generateUpgrade(Location location, DungeonPlayer generator, Consumer<Item> consumer) {
        return generateUpgrade(location, UpgradeType.getRandomWeighted(), generator, consumer);
    }
    public @NotNull Item generateUpgrade(Location location, @NotNull UpgradeType type, DungeonPlayer generator) {
        return generateUpgrade(location, type, generator, item -> {});
    }
    public @NotNull Item generateUpgrade(Location location, @NotNull UpgradeType type, DungeonPlayer generator, Consumer<Item> consumer) {
        GenerateUpgradeAction action = new GenerateUpgradeAction(generator, type, plugin);
        plugin.getUpgradeEventManager().emit(action);
        return dropUpgrade(location, action.getGenerated(), consumer);
    }
}
