package com.datasiqn.arcadia.events;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaKeys;
import com.datasiqn.arcadia.DamageHelper;
import com.datasiqn.arcadia.items.ArcadiaItem;
import com.datasiqn.arcadia.items.ItemType;
import com.datasiqn.arcadia.items.stats.ItemAttribute;
import com.datasiqn.arcadia.managers.PlayerManager;
import com.datasiqn.arcadia.players.PlayerEquipment;
import com.datasiqn.arcadia.players.PlayerStats;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
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

        PlayerStats playerStats = plugin.getPlayerManager().getPlayerData(event.getPlayer()).playerStats();
        EntityEquipment equipment = event.getPlayer().getEquipment();
        if (equipment != null) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack item = equipment.getItem(slot);
                playerStats.getEquipment().setItem(slot, new ArcadiaItem(item));
            }
        }
        playerStats.updateValues();
    }

    @EventHandler
    public void onPlayerLeave(@NotNull PlayerQuitEvent event) {
        AttributeInstance attackSpeedAttribute = event.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        assert attackSpeedAttribute != null;
        attackSpeedAttribute.setBaseValue(4);

        AttributeInstance healthAttribute = event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH);
        assert healthAttribute != null;
        healthAttribute.setBaseValue(20);

        plugin.getPlayerManager().removePlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerSwitchSlot(@NotNull PlayerItemHeldEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        PlayerStats playerStats = plugin.getPlayerManager().getPlayerData(player).playerStats();
        ItemStack item = player.getInventory().getItem(event.getNewSlot());
        if (item == null) item = new ItemStack(Material.AIR);
        PlayerEquipment equipment = playerStats.getEquipment();
        equipment.setItemInMainHand(new ArcadiaItem(item));
        playerStats.updateValues();
    }

    @EventHandler
    public void onPlayerClickArmor(@NotNull InventoryClickEvent event) {
        if (event.isCancelled()) return;
        if (event.getClickedInventory() == null) return;
        if (event.getAction() == InventoryAction.NOTHING) return;
        if (event.getSlotType() != InventoryType.SlotType.ARMOR) return;
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

    public void updateArmor(HumanEntity player) {
        PlayerManager.PlayerData playerData = plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData == null) return;
        PlayerEquipment playerEquipment = playerData.playerStats().getEquipment();
        EntityEquipment equipment = player.getEquipment();
        if (equipment == null) return;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot == EquipmentSlot.HAND || slot == EquipmentSlot.OFF_HAND) continue;

            if (playerEquipment.getItem(slot).build().equals(equipment.getItem(slot))) continue;
            playerEquipment.setItem(slot, new ArcadiaItem(equipment.getItem(slot)));
        }
        playerData.playerStats().updateValues();
    }

    @EventHandler
    public void onPlayerRespawn(@NotNull PlayerRespawnEvent event) {
        plugin.getPlayerManager().getPlayerData(event.getPlayer()).playerStats().heal();
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

        PlayerStats playerStats = plugin.getPlayerManager().getPlayerData(player).playerStats();

        ArcadiaItem bowItem = new ArcadiaItem(event.getBow());
        if (bowItem.getItemData().getItemType() != ItemType.BOW) return;

        PersistentDataContainer pdc = event.getProjectile().getPersistentDataContainer();
        com.datasiqn.arcadia.items.stats.AttributeInstance damageAttribute = bowItem.getItemMeta().getItemStats().getAttribute(ItemAttribute.DAMAGE);
        double damageValue = damageAttribute == null ? 1 : damageAttribute.getValue();
        pdc.set(ArcadiaKeys.ARROW_DAMAGE, PersistentDataType.DOUBLE, damageValue * DamageHelper.getStrengthMultiplier(playerStats.getStrength()) * event.getForce());
    }
}
