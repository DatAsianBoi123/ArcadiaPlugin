package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.commands.argument.ArcadiaArgumentType;
import com.datasiqn.arcadia.menu.handlers.RecipeMenuHandler;
import com.datasiqn.commandcore.commands.builder.ArgumentBuilder;
import com.datasiqn.commandcore.commands.builder.CommandBuilder;
import com.datasiqn.menuapi.MenuApi;
import org.bukkit.inventory.Inventory;

public class CommandViewRecipe {
    public CommandBuilder getCommand() {
        return new CommandBuilder()
                .permission(ArcadiaPermission.PERMISSION_USE_RECIPE)
                .description("Views a custom Arcadia Recipe")
                .then(ArgumentBuilder.argument(ArcadiaArgumentType.RECIPE, "recipe")
                        .requiresPlayer()
                        .executes(context -> {
                            RecipeMenuHandler gui = new RecipeMenuHandler(context.getArguments().get(0, ArcadiaArgumentType.RECIPE).unwrap());
                            Inventory inventory = gui.createInventory();
                            MenuApi.getInstance().getMenuManager().registerHandler(inventory, gui);
                            context.getSource().getPlayer().unwrap().openInventory(inventory);
                        }));
    }
}
