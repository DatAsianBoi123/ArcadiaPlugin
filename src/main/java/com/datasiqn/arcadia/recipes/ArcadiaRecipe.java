package com.datasiqn.arcadia.recipes;

import com.datasiqn.arcadia.recipes.craftingtable.*;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;

@SuppressWarnings("unused")
public enum ArcadiaRecipe {
    ENCHANTED_STICK(new EnchantedStickRecipe()),
    CROOKED_SWORD(new CrookedSwordRecipe()),
    ULTIMATUM(new UltimatumRecipe()),
    AWAKENED_CORE(new AwakenedCoreRecipe());

    private final CraftingRecipe recipe;

    ArcadiaRecipe(@NotNull CraftingRecipe recipe) {
        this.recipe = recipe;
    }

    public CraftingRecipe getRecipeData() {
        return recipe;
    }

    @Nullable
    public static ArcadiaRecipe findApplicableRecipe(ItemStack[] recipe) {
        Optional<ArcadiaRecipe> first = Arrays.stream(values()).filter(recipe1 -> recipe1.getRecipeData().canCraft(recipe)).findFirst();
        return first.orElse(null);
    }
}
