package com.datasiqn.arcadia.guis;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ArcadiaGUI implements InventoryHolder {
    private final int size;
    private final String name;

    public ArcadiaGUI(int size, @Nullable String name) {
        this.size = size;
        this.name = name;
    }

    public abstract void init(Inventory inv);

    public abstract void clickEvent(@NotNull InventoryInteractEvent event);

    public void closeEvent(@NotNull InventoryCloseEvent event) {}

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inventory = name == null ? Bukkit.createInventory(this, size) : Bukkit.createInventory(this, size, name);
        init(inventory);
        return inventory;
    }
}
