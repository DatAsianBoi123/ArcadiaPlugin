package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.commands.arguments.ArgumentType;
import com.datasiqn.arcadia.commands.builder.ArgumentBuilder;
import com.datasiqn.arcadia.commands.builder.CommandBuilder;
import com.datasiqn.arcadia.guis.ViewRecipeGUI;
import com.datasiqn.arcadia.recipes.ArcadiaRecipe;
import org.bukkit.entity.Player;

public class CommandViewRecipe {
    public ArcadiaCommand getCommand() {
        return new CommandBuilder<>(Player.class)
                .permission(ArcadiaPermission.PERMISSION_USE_RECIPE)
                .description("Views a custom Arcadia Recipe")
                .then(ArgumentBuilder.<Player, ArcadiaRecipe>argument(ArgumentType.RECIPE, "recipe")
                        .executes(context -> context.sender().get().openInventory(new ViewRecipeGUI(context.parseArgument(ArgumentType.RECIPE, 0)).getInventory())))
                .build();
    }
}
