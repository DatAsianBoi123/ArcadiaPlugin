package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.commands.arguments.ArcadiaArgumentType;
import com.datasiqn.arcadia.guis.ViewRecipeGUI;
import com.datasiqn.arcadia.recipes.ArcadiaRecipe;
import com.datasiqn.commandcore.commands.Command;
import com.datasiqn.commandcore.commands.builder.ArgumentBuilder;
import com.datasiqn.commandcore.commands.builder.CommandBuilder;
import org.bukkit.entity.Player;

public class CommandViewRecipe {
    public Command getCommand() {
        return new CommandBuilder<>(Player.class)
                .permission(ArcadiaPermission.PERMISSION_USE_RECIPE)
                .description("Views a custom Arcadia Recipe")
                .then(ArgumentBuilder.<Player, ArcadiaRecipe>argument(ArcadiaArgumentType.RECIPE, "recipe")
                        .executes(context -> context.getSender().openInventory(new ViewRecipeGUI(context.parseArgument(ArcadiaArgumentType.RECIPE, 0)).getInventory())))
                .build();
    }
}
