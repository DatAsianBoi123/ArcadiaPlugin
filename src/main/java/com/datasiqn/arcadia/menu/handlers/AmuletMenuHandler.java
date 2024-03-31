package com.datasiqn.arcadia.menu.handlers;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.amulet.Amulet;
import com.datasiqn.arcadia.amulet.PowerStone;
import com.datasiqn.arcadia.player.ArcadiaSender;
import com.datasiqn.arcadia.player.Experience;
import com.datasiqn.arcadia.player.PlayerData;
import com.datasiqn.arcadia.util.ItemUtil;
import com.datasiqn.menuapi.inventory.MenuHandler;
import com.datasiqn.menuapi.inventory.item.MenuButton;
import com.datasiqn.menuapi.inventory.item.MenuItem;
import com.datasiqn.menuapi.inventory.item.StaticMenuItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class AmuletMenuHandler extends MenuHandler {
    public static final int[] AMULET_SLOTS = {
            10, 11, 12,
            19, 20, 21,
            28, 29, 30,
    };

    private static final ItemStack AMULET_SLOT_ITEM = createAmuletSlotItem();

    private static final PowerStone[] POWER_STONES = PowerStone.values();

    private final Arcadia plugin;

    public AmuletMenuHandler(Arcadia plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onClose(@NotNull InventoryCloseEvent event) {
        super.onClose(event);

        plugin.saveConfig();
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(event.getPlayer().getUniqueId());
        if (playerData == null) return;
        playerData.updateValues();
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        super.onClick(event);

        event.setCancelled(true);
    }

    @Override
    public void onDrag(@NotNull InventoryDragEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void populate(@NotNull HumanEntity humanEntity, Inventory inventory) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(humanEntity.getUniqueId());
        if (playerData == null) {
            new ArcadiaSender<>(humanEntity).sendError("Failed to get player data");
            return;
        }
        Amulet amulet = playerData.getEquipment().getAmulet();

        ItemStack background = ItemUtil.createEmpty(Material.GRAY_STAINED_GLASS_PANE);
        ItemStack separator = ItemUtil.createEmpty(Material.BLACK_STAINED_GLASS_PANE);
        for (int i = 0; i < inventory.getSize(); i++) {
            MenuItem menuItem = new StaticMenuItem(background);
            if (i % 9 == 5) menuItem = new StaticMenuItem(separator);
            else if (i % 9 > 5) {
                int powerStoneIndex = i % 9 - 6 + i / 9 * 3;
                if (powerStoneIndex < POWER_STONES.length) menuItem = createClickablePowerStone(playerData, POWER_STONES[powerStoneIndex], amulet, inventory);
            }
            setItem(i, menuItem);
        }

        for (int i = 0; i < AMULET_SLOTS.length; i++) {
            setItem(AMULET_SLOTS[i], createAmuletSlot(amulet, i, inventory));
        }

        ItemStack clearAllItem = new ItemStack(Material.BARRIER);
        ItemMeta meta = clearAllItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RED + "Clear All");
            clearAllItem.setItemMeta(meta);
        }
        setItem(38, new MenuButton(clearAllItem)
                .onClick(event -> {
                    amulet.clear();
                    for (int i = 0; i < AMULET_SLOTS.length; i++) {
                        inventory.setItem(AMULET_SLOTS[i], createAmuletSlot(amulet, i, inventory).getIcon());
                    }
                }));
    }

    @Override
    public @NotNull Inventory createInventory() {
        return Bukkit.createInventory(null, 54, "Amulet");
    }

    private MenuItem createAmuletSlot(@NotNull Amulet amulet, int amuletIndex, Inventory inventory) {
        PowerStone powerStone = amulet.get(amuletIndex);
        ItemStack icon;
        if (amuletIndex >= amulet.getTotalSlots()) icon = createLockedSlotItem(amulet.getLevelForSlots(amuletIndex + 1));
        else if (powerStone == null) icon = AMULET_SLOT_ITEM;
        else icon = powerStone.getItem().build();
        return new MenuButton(icon)
                .onClick(event -> {
                    if (amulet.get(amuletIndex) == null) return;
                    amulet.delete(amuletIndex);
                    inventory.setItem(event.getSlot(), AMULET_SLOT_ITEM);
                });
    }

    private MenuItem createClickablePowerStone(@NotNull PlayerData player, @NotNull PowerStone powerStone, Amulet amulet, Inventory inventory) {
        ItemStack item;
        int levelRequirement = powerStone.getData().getLevelRequirement();
        Experience playerXp = player.getXp();
        if (playerXp.getLevel() >= levelRequirement) {
            item = powerStone.getItem().build();
        } else {
            item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.RED + "Requires Level " + levelRequirement);
                item.setItemMeta(meta);
            }
        }
        return new MenuButton(item)
                .onClick(event -> {
                    if (playerXp.getLevel() < levelRequirement) return;
                    int addedIndex = amulet.add(powerStone);
                    if (addedIndex == -1) return;
                    inventory.setItem(AMULET_SLOTS[addedIndex], item);
                });
    }

    private static @NotNull ItemStack createAmuletSlotItem() {
        ItemStack itemStack = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return itemStack;
        meta.setDisplayName(ChatColor.GREEN + "Amulet Slot");
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    private static @NotNull ItemStack createLockedSlotItem(int level) {
        ItemStack itemStack = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return itemStack;
        meta.setDisplayName(ChatColor.RED + "Requires Level " + level);
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
