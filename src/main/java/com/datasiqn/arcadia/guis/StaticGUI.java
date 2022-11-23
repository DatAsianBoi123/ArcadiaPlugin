package com.datasiqn.arcadia.guis;

import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class StaticGUI extends ArcadiaGUI {
    public StaticGUI(int size, @Nullable String name) {
        super(size, name);
    }
    public StaticGUI(InventoryType inventoryType, @Nullable String name) {
        super(inventoryType, name);
    }

    @Override
    public void clickEvent(@NotNull InventoryInteractEvent event) {
        event.setCancelled(true);
    }
}
