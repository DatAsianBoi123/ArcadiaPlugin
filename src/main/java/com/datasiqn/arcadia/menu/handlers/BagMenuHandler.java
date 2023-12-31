package com.datasiqn.arcadia.menu.handlers;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.dungeon.DungeonPlayer;
import com.datasiqn.arcadia.upgrade.Upgrade;
import com.datasiqn.arcadia.util.ItemUtil;
import com.datasiqn.menuapi.inventory.MenuHandler;
import com.datasiqn.menuapi.inventory.item.MenuButton;
import com.datasiqn.menuapi.inventory.item.StaticMenuItem;
import com.datasiqn.schedulebuilder.ScheduleBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BagMenuHandler extends MenuHandler {
    private final Arcadia plugin;

    public BagMenuHandler(Arcadia plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        super.onClick(event);

        event.setCancelled(true);
    }

    @Override
    public void onDrag(@NotNull InventoryDragEvent event) {
        super.onDrag(event);

        event.setCancelled(true);
    }

    @Override
    public void populate(@NotNull HumanEntity humanEntity, Inventory inventory) {
        DungeonPlayer dungeonPlayer = plugin.getDungeonManager().getDungeonPlayer(humanEntity);
        if (dungeonPlayer == null) return;

        ItemStack empty = ItemUtil.createEmpty(Material.GRAY_STAINED_GLASS_PANE);
        for (int i = 0; i < 9; i++) setItem(53 - i, new StaticMenuItem(empty));

        ItemStack closeItem = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeItem.getItemMeta();
        if (closeMeta == null) return;
        closeMeta.setDisplayName(ChatColor.RED + "Close");
        closeItem.setItemMeta(closeMeta);
        setItem(49, new MenuButton(closeItem).onClick(event -> ScheduleBuilder.create().executes(runnable -> event.getWhoClicked().closeInventory()).run(plugin)));

        List<Upgrade> upgrades = dungeonPlayer.getUpgrades();
        List<ItemStack> items = new ArrayList<>();
        for (Upgrade upgrade : upgrades) {
            int amount = upgrade.getAmount();
            int totalItems = amount / 65 + 1;
            for (int j = 0; j < totalItems; j++) {
                ItemStack itemStack = upgrade.getType().getData().toItemStack(amount - 64 * j, UUID.randomUUID());
                ItemMeta meta = itemStack.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(meta.getDisplayName() + ChatColor.GRAY + " (" + amount + ")");
                    itemStack.setItemMeta(meta);
                }
                items.add(itemStack);
            }
        }
        for (int i = 0; i < items.size(); i++) {
            setItem(i, new StaticMenuItem(items.get(i)));
        }
    }

    @Override
    public @NotNull Inventory createInventory() {
        return Bukkit.createInventory(null, 54, "Item Bag");
    }
}
