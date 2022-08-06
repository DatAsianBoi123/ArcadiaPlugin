package com.datasiqn.arcadia.guis;

import com.datasiqn.arcadia.Arcadia;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public enum GUIType {
    CRAFTING(new CraftingGUI(Arcadia.getPlugin(Arcadia.class))),
    ANVIL(new AnvilGUI(Arcadia.getPlugin(Arcadia.class)));

    private final ArcadiaGUI gui;

    GUIType(ArcadiaGUI gui) {
        this.gui = gui;
    }

    public @NotNull Inventory getInventory() {
        return gui.getInventory();
    }
}
