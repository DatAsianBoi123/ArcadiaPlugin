package com.datasiqn.arcadia.guis;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ArcadiaGUI implements InventoryHolder {
    protected Inventory inv;

    public ArcadiaGUI(int size, @Nullable String name) {
        inv = name == null ? Bukkit.createInventory(this, size) : Bukkit.createInventory(this, size, name);
    }
    public ArcadiaGUI(InventoryType inventoryType, @Nullable String name) {
        inv = name == null ? Bukkit.createInventory(this, inventoryType) : Bukkit.createInventory(this, inventoryType, name);
    }

    public void openEvent(@NotNull InventoryOpenEvent event) {}

    public void clickEvent(@NotNull InventoryInteractEvent event) {}

    public void closeEvent(@NotNull InventoryCloseEvent event) {}

    @NotNull
    @Override
    public Inventory getInventory() {
        return inv;
    }
}
