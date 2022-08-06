package com.datasiqn.arcadia.events;

import com.datasiqn.arcadia.guis.GUIType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.jetbrains.annotations.NotNull;

public class GUIEvents implements Listener {
    @EventHandler
    public void onOpenCraftingTable(@NotNull InventoryOpenEvent event) {
        if (event.getInventory().getType() == InventoryType.WORKBENCH) {
            event.setCancelled(true);
            event.getPlayer().openInventory(GUIType.CRAFTING.getInventory());
        }

        if (event.getInventory().getType() == InventoryType.ANVIL) {
            event.setCancelled(true);
            event.getPlayer().openInventory(GUIType.ANVIL.getInventory());
        }
    }

    @EventHandler
    public void onCraft(@NotNull PrepareItemCraftEvent event) {
        if (event.getInventory().getMatrix().length == 4) event.getInventory().setResult(null);
    }
}
