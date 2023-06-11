package com.datasiqn.arcadia.menu;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.menu.handlers.AmuletMenuHandler;
import com.datasiqn.arcadia.menu.handlers.AnvilMenuHandler;
import com.datasiqn.arcadia.menu.handlers.CraftingMenuHandler;
import com.datasiqn.menuapi.MenuApi;
import com.datasiqn.menuapi.inventory.MenuHandler;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public enum MenuType {
    ANVIL(AnvilMenuHandler::new),
    CRAFTING(CraftingMenuHandler::new),
    AMULET(AmuletMenuHandler::new),
    ;

    private final Function<Arcadia, MenuHandler> menuCreator;

    MenuType(Function<Arcadia, MenuHandler> menuCreator) {
        this.menuCreator = menuCreator;
    }

    public void openInventory(@NotNull HumanEntity player, Arcadia plugin) {
        MenuHandler handler = menuCreator.apply(plugin);
        Inventory inventory = handler.createInventory();
        MenuApi.getInstance().getMenuManager().registerHandler(inventory, handler);
        player.openInventory(inventory);
    }
}
