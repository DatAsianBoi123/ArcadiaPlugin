package com.datasiqn.arcadia.recipes.craftingtable;

import com.datasiqn.arcadia.items.ArcadiaItem;
import com.datasiqn.arcadia.items.types.ArcadiaMaterial;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class EnchantedStickRecipe implements CraftingRecipe {
    private static final ItemStack[] RECIPE = new ItemStack[9];
    private static final ArcadiaItem RESULT = new ArcadiaItem(ArcadiaMaterial.ENCHANTED_STICK);
    private static final ItemStack ITEM_STACK_RESULT = RESULT.asCraftingResult();

    static {
        Arrays.fill(RECIPE, ArcadiaItem.from(Material.STICK, 32));
    }

    @Override
    public ItemStack @NotNull [] getRecipe() {
        return RECIPE;
    }

    @Override
    public @NotNull ArcadiaItem getResult() {
        return new ArcadiaItem(RESULT);
    }

    @Override
    public @NotNull ItemStack getItemStackResult() {
        return ITEM_STACK_RESULT;
    }
}
