package com.datasiqn.arcadia.listeners;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.menu.MenuType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.jetbrains.annotations.NotNull;

public class GuiListener implements Listener {
    private final Arcadia plugin;

    public GuiListener(Arcadia plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onOpenCustomMenu(@NotNull InventoryOpenEvent event) {
        if (event.getInventory().getType() == InventoryType.WORKBENCH) {
            event.setCancelled(true);
            MenuType.CRAFTING.openInventory(event.getPlayer(), plugin);
        }

        if (event.getInventory().getType() == InventoryType.ANVIL) {
            event.setCancelled(true);
            MenuType.ANVIL.openInventory(event.getPlayer(), plugin);
        }
    }

    @EventHandler
    public void onCraft(@NotNull PrepareItemCraftEvent event) {
        if (event.getInventory().getMatrix().length == 4) event.getInventory().setResult(null);
    }
}
