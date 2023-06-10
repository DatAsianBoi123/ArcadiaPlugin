package com.datasiqn.arcadia.guis;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.menuapi.MenuApi;
import com.datasiqn.menuapi.inventory.MenuHandler;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public enum GUIType {
    ANVIL(AnvilGUI::new),
    CRAFTING(CraftingGUI::new),
    AMULET(AmuletGUI::new),
    ;

    private final Function<Arcadia, MenuHandler> menuCreator;

    GUIType(Function<Arcadia, MenuHandler> menuCreator) {
        this.menuCreator = menuCreator;
    }

    public void openInventory(@NotNull HumanEntity player, Arcadia plugin) {
        MenuHandler handler = menuCreator.apply(plugin);
        Inventory inventory = handler.createInventory();
        MenuApi.getInstance().getMenuManager().registerHandler(inventory, handler);
        player.openInventory(inventory);
    }
}
