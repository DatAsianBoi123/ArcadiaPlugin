package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.menu.handlers.RecipeMenuHandler;
import com.datasiqn.arcadia.recipe.ArcadiaRecipe;
import com.datasiqn.commandcore.command.annotation.AnnotationCommand;
import com.datasiqn.commandcore.command.annotation.Argument;
import com.datasiqn.commandcore.command.annotation.CommandDescription;
import com.datasiqn.commandcore.command.annotation.Executor;
import com.datasiqn.menuapi.MenuApi;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@CommandDescription(name = "viewrecipe", description = "Views a custom Arcadia Recipe", permission = ArcadiaPermission.PERMISSION_USE_RECIPE)
public class CommandViewRecipe implements AnnotationCommand {
    @Executor
    public void viewRecipe(Player player,
                           @Argument(name = "recipe") ArcadiaRecipe recipe) {
        RecipeMenuHandler gui = new RecipeMenuHandler(recipe);
        Inventory inventory = gui.createInventory();
        MenuApi.getInstance().getMenuManager().registerHandler(inventory, gui);
        player.openInventory(inventory);
    }
}
