package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.commands.argument.ArcadiaArgumentType;
import com.datasiqn.arcadia.menu.handlers.RecipeMenuHandler;
import com.datasiqn.commandcore.command.builder.ArgumentBuilder;
import com.datasiqn.commandcore.command.builder.CommandBuilder;
import com.datasiqn.menuapi.MenuApi;
import org.bukkit.inventory.Inventory;

public class CommandViewRecipe {
    public CommandBuilder getCommand() {
        return new CommandBuilder("viewrecipe")
                .permission(ArcadiaPermission.PERMISSION_USE_RECIPE)
                .description("Views a custom Arcadia Recipe")
                .then(ArgumentBuilder.argument(ArcadiaArgumentType.RECIPE, "recipe")
                        .requiresPlayer()
                        .executes((context, source, arguments) -> {
                            RecipeMenuHandler gui = new RecipeMenuHandler(arguments.get(0, ArcadiaArgumentType.RECIPE));
                            Inventory inventory = gui.createInventory();
                            MenuApi.getInstance().getMenuManager().registerHandler(inventory, gui);
                            source.getPlayer().openInventory(inventory);
                        }));
    }
}
