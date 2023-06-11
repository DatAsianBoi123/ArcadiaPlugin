package com.datasiqn.arcadia.recipe.craftingtable;

import com.datasiqn.arcadia.item.ArcadiaItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CraftingRecipe {
    @Nullable ItemStack @NotNull [] getRecipe();

    default boolean canCraft(@Nullable ItemStack @NotNull [] itemMatrix) {
        ItemStack[] recipe = getRecipe();
        for (int i = 0; i < recipe.length; i++) {
            if (itemMatrix[i] == null && recipe[i] == null) continue;
            if (itemMatrix[i] == null || recipe[i] == null) return false;
            if (!new ArcadiaItem(itemMatrix[i]).isSimilar(new ArcadiaItem(recipe[i]))) return false;
            if (itemMatrix[i].getAmount() < recipe[i].getAmount()) return false;
        }
        return true;
    }

    @NotNull ArcadiaItem getResult();

    @NotNull ItemStack getItemStackResult();
}
