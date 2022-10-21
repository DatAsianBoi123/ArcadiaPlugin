package com.datasiqn.arcadia.guis;

import com.datasiqn.arcadia.Arcadia;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public enum GUIType {
    ANVIL(AnvilGUI::new),
    CRAFTING(CraftingGUI::new),
    AMULET(AmuletGUI::new),
    ;

    private final Function<Arcadia, ArcadiaGUI> guiFunction;

    GUIType(Function<Arcadia, ArcadiaGUI> guiFunction) {
        this.guiFunction = guiFunction;
    }

    public @NotNull Inventory createInventory(Arcadia plugin) {
        return guiFunction.apply(plugin).getInventory();
    }
}
