package com.datasiqn.arcadia.listeners;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaTag;
import com.datasiqn.arcadia.DamageHelper;
import com.datasiqn.arcadia.dungeon.DungeonPlayer;
import com.datasiqn.arcadia.item.ArcadiaItem;
import com.datasiqn.arcadia.item.ItemRarity;
import com.datasiqn.arcadia.item.stat.ItemAttribute;
import com.datasiqn.arcadia.item.type.ItemType;
import com.datasiqn.arcadia.managers.DungeonManager;
import com.datasiqn.arcadia.managers.PlayerManager;
import com.datasiqn.arcadia.player.ArcadiaPacketListener;
import com.datasiqn.arcadia.player.PlayerData;
import com.datasiqn.arcadia.player.PlayerEquipment;
import com.datasiqn.arcadia.upgrade.actions.ShootBowAction;
import com.datasiqn.arcadia.util.PdcUtil;
import com.datasiqn.schedulebuilder.ScheduleBuilder;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class PlayerListener implements Listener {
    private final Arcadia plugin;
    private final PlayerManager playerManager;

    @Contract(pure = true)
    public PlayerListener(@NotNull Arcadia plugin) {
        this.plugin = plugin;
        this.playerManager = plugin.getPlayerManager();
    }

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if (attribute != null) attribute.setBaseValue(PlayerData.DEFAULT_ATTACK_SPEED);

        PlayerData playerData = playerManager.getPlayerData(player);
        playerData.loadData();
        EntityEquipment equipment = player.getEquipment();
        if (equipment != null) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack item = equipment.getItem(slot);
                playerData.getEquipment().setItem(slot, new ArcadiaItem(item));
            }
        }
        playerData.updateValues();
        playerData.updateLevel();
        plugin.getScoreboardManager().createScoreboard(player);
        plugin.getScoreboardManager().updateScoreboard(player);
        ItemRarity.createTeams(player.getScoreboard());

        if (player.getWorld().getName().startsWith(DungeonManager.DUNGEON_WORLD_PREFIX)) {
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        }

        plugin.getNpcManager().updateForPlayer(player);

        ServerGamePacketListenerImpl oldConnection = ((CraftPlayer) player).getHandle().connection;
        ((CraftPlayer) player).getHandle().connection = new ArcadiaPacketListener(oldConnection, plugin);
    }

    @EventHandler
    public void onPlayerLeave(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        AttributeInstance attackSpeedAttribute = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if (attackSpeedAttribute != null) {
            attackSpeedAttribute.setBaseValue(4);
            attackSpeedAttribute.getModifiers().forEach(attackSpeedAttribute::removeModifier);
        }

        AttributeInstance healthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (healthAttribute != null) healthAttribute.setBaseValue(20);

        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        if (scoreboardManager != null) player.setScoreboard(scoreboardManager.getMainScoreboard());

        new Thread(playerManager.getPlayerData(player)::saveData).start();
        plugin.getNpcManager().deselectNpc(player);
        playerManager.removePlayer(player);
    }

    @EventHandler
    public void onPlayerPickupXp(@NotNull PlayerExpChangeEvent event) {
        PlayerData playerData = playerManager.getPlayerData(event.getPlayer());
        playerData.addXp(event.getAmount());
        plugin.getScoreboardManager().updateScoreboard(playerData.getPlayer());

        event.setAmount(0);
    }

    @EventHandler
    public void onPlayerBreakBlock(@NotNull BlockBreakEvent event) {
        if (event.getPlayer().isOp() && event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPlaceBlock(@NotNull BlockPlaceEvent event) {
        if (event.getPlayer().isOp() && event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerSwitchSlot(@NotNull PlayerItemHeldEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        PlayerData playerData = playerManager.getPlayerData(player);
        ItemStack item = player.getInventory().getItem(event.getNewSlot());
        if (item == null) item = new ItemStack(Material.AIR);
        PlayerEquipment equipment = playerData.getEquipment();
        equipment.setItemInMainHand(new ArcadiaItem(item));
        playerData.updateValues();
    }

    @EventHandler
    public void onPlayerClickArmor(@NotNull InventoryClickEvent event) {
        if (event.isCancelled()) return;
        if (event.getClickedInventory() == null) return;
        if (event.getAction() == InventoryAction.NOTHING || event.getAction() == InventoryAction.UNKNOWN) return;
        ScheduleBuilder.create().executes(runnable -> updateArmor(event.getWhoClicked())).run(plugin);
    }

    @EventHandler
    public void onPlayerEquipArmor(@NotNull PlayerInteractEvent event) {
        if (event.useItemInHand() == Event.Result.DENY) return;
        if (event.getAction() == Action.PHYSICAL) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ScheduleBuilder.create().executes(runnable -> updateArmor(event.getPlayer())).run(plugin);
    }

    @EventHandler
    public void onDispenserEquipArmor(@NotNull BlockDispenseArmorEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getTargetEntity() instanceof Player player)) return;
        ScheduleBuilder.create().executes(runnable -> updateArmor(player)).run(plugin);
    }

    public void updateArmor(@NotNull HumanEntity player) {
        PlayerData playerData = playerManager.getPlayerData(player.getUniqueId());
        if (playerData == null) return;
        PlayerEquipment playerEquipment = playerData.getEquipment();
        EntityEquipment equipment = player.getEquipment();
        if (equipment == null) return;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot == EquipmentSlot.HAND || slot == EquipmentSlot.OFF_HAND) continue;

            if (playerEquipment.getItem(slot).build().equals(equipment.getItem(slot))) continue;
            playerEquipment.setItem(slot, new ArcadiaItem(equipment.getItem(slot)));
        }
        playerData.updateValues();
    }

    @EventHandler
    public void onPlayerRespawn(@NotNull PlayerRespawnEvent event) {
        playerManager.getPlayerData(event.getPlayer()).heal();
    }

    @EventHandler
    public void onPlayerRegen(@NotNull EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerLoseHunger(@NotNull FoodLevelChangeEvent event) {
        event.setCancelled(true);
        event.getEntity().setFoodLevel(20);
        event.getEntity().setSaturation(5);
    }

    @EventHandler
    public void onPlayerShootBow(@NotNull EntityShootBowEvent event) {
        if (event.getBow() == null) return;
        if (!(event.getEntity() instanceof Player player)) return;

        PlayerData playerData = playerManager.getPlayerData(player);

        ArcadiaItem bowItem = new ArcadiaItem(event.getBow());
        if (bowItem.getData().getType() != ItemType.BOW) return;

        if (!(event.getProjectile() instanceof Arrow arrow)) return;
        PersistentDataContainer pdc = arrow.getPersistentDataContainer();
        com.datasiqn.arcadia.item.stat.AttributeInstance damageAttribute = bowItem.getItemMeta().getItemStats().getAttribute(ItemAttribute.DAMAGE);
        double damageValue = damageAttribute == null ? 1 : damageAttribute.getValue();
        double damage = damageValue + DamageHelper.getStrengthBonus(playerData.getStrength(), damageValue) * event.getForce();
        PdcUtil.set(pdc, ArcadiaTag.ARROW_DAMAGE, damage);

        DungeonPlayer dungeonPlayer = plugin.getDungeonManager().getDungeonPlayer(playerData);
        if (dungeonPlayer != null) {
            plugin.getUpgradeEventManager().emit(new ShootBowAction(dungeonPlayer, arrow, plugin));
        }
    }
}
