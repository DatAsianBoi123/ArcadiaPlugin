package com.datasiqn.arcadia.recipe;

import com.datasiqn.arcadia.recipe.craftingtable.*;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@SuppressWarnings("unused")
public enum ArcadiaRecipe {
    ENCHANTED_STICK(new EnchantedStickRecipe()),
    CROOKED_SWORD(new CrookedSwordRecipe()),
    ULTIMATUM(new UltimatumRecipe()),
    AWAKENED_CORE(new AwakenedCoreRecipe()),
    ;

    private final CraftingRecipe recipe;

    ArcadiaRecipe(@NotNull CraftingRecipe recipe) {
        this.recipe = recipe;
    }

    public CraftingRecipe getRecipeData() {
        return recipe;
    }

    @Nullable
    public static ArcadiaRecipe findApplicableRecipe(ItemStack[] recipe) {
        return Arrays.stream(values()).filter(arcadiaRecipe -> arcadiaRecipe.getRecipeData().canCraft(recipe)).findFirst().orElse(null);
    }
}
