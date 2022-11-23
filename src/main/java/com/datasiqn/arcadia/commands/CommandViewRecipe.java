package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.commands.arguments.ArcadiaArgumentType;
import com.datasiqn.arcadia.guis.ViewRecipeGUI;
import com.datasiqn.commandcore.commands.builder.ArgumentBuilder;
import com.datasiqn.commandcore.commands.builder.CommandBuilder;

public class CommandViewRecipe {
    public CommandBuilder getCommand() {
        return new CommandBuilder()
                .permission(ArcadiaPermission.PERMISSION_USE_RECIPE)
                .description("Views a custom Arcadia Recipe")
                .then(ArgumentBuilder.argument(ArcadiaArgumentType.RECIPE, "recipe")
                        .requiresPlayer()
                        .executes(context -> context.getSource().getPlayer().unwrap().openInventory(new ViewRecipeGUI(context.getArguments().get(0, ArcadiaArgumentType.RECIPE).unwrap()).getInventory())));
    }
}
