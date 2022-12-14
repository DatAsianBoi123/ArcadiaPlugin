package com.datasiqn.arcadia.events;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaKeys;
import com.datasiqn.arcadia.DamageHelper;
import com.datasiqn.arcadia.items.ArcadiaItem;
import com.datasiqn.arcadia.items.stats.ItemAttribute;
import com.datasiqn.arcadia.items.type.ItemType;
import com.datasiqn.arcadia.players.PlayerData;
import com.datasiqn.arcadia.players.PlayerEquipment;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
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
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class PlayerEvents implements Listener {
    private final Arcadia plugin;

    public PlayerEvents(Arcadia plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        AttributeInstance attribute = event.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        assert attribute != null;
        attribute.setBaseValue(16);

        PlayerData playerData = plugin.getPlayerManager().getPlayerData(event.getPlayer());
        playerData.loadData();
        EntityEquipment equipment = event.getPlayer().getEquipment();
        if (equipment != null) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack item = equipment.getItem(slot);
                playerData.getEquipment().setItem(slot, new ArcadiaItem(item));
            }
        }
        playerData.updateValues();
        playerData.updateLevel();
    }

    @EventHandler
    public void onPlayerLeave(@NotNull PlayerQuitEvent event) {
        AttributeInstance attackSpeedAttribute = event.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        assert attackSpeedAttribute != null;
        attackSpeedAttribute.setBaseValue(4);

        AttributeInstance healthAttribute = event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH);
        assert healthAttribute != null;
        healthAttribute.setBaseValue(20);

        plugin.getPlayerManager().getPlayerData(event.getPlayer()).saveData();
        plugin.getPlayerManager().removePlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerPickupXp(@NotNull PlayerExpChangeEvent event) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(event.getPlayer());
        playerData.setTotalXp(playerData.getTotalXp() + event.getAmount());

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
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
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
        plugin.runAfterOneTick(() -> updateArmor(event.getWhoClicked()));
    }

    @EventHandler
    public void onPlayerEquipArmor(@NotNull PlayerInteractEvent event) {
        if (event.useItemInHand() == Event.Result.DENY) return;
        if (event.getAction() == Action.PHYSICAL) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        plugin.runAfterOneTick(() -> updateArmor(event.getPlayer()));
    }

    @EventHandler
    public void onDispenserEquipArmor(@NotNull BlockDispenseArmorEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getTargetEntity() instanceof Player player)) return;
        plugin.runAfterOneTick(() -> updateArmor(player));
    }

    public void updateArmor(@NotNull HumanEntity player) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player.getUniqueId());
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
        plugin.getPlayerManager().getPlayerData(event.getPlayer()).heal();
    }

    @EventHandler
    public void onEntityRegen(@NotNull EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerLoseHunger(@NotNull FoodLevelChangeEvent event) {
        event.setCancelled(true);
        event.getEntity().setFoodLevel(20);
    }

    @EventHandler
    public void onPlayerShootBow(@NotNull EntityShootBowEvent event) {
        if (event.getBow() == null) return;
        if (!(event.getEntity() instanceof Player player)) return;

        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);

        ArcadiaItem bowItem = new ArcadiaItem(event.getBow());
        if (bowItem.getItemData().getItemType() != ItemType.BOW) return;

        PersistentDataContainer pdc = event.getProjectile().getPersistentDataContainer();
        com.datasiqn.arcadia.items.stats.AttributeInstance damageAttribute = bowItem.getItemMeta().getItemStats().getAttribute(ItemAttribute.DAMAGE);
        double damageValue = damageAttribute == null ? 1 : damageAttribute.getValue();
        pdc.set(ArcadiaKeys.ARROW_DAMAGE, PersistentDataType.DOUBLE, damageValue + DamageHelper.getStrengthBonus(playerData.getStrength(), damageValue) * event.getForce());
    }
}
