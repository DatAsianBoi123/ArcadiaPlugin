package com.datasiqn.arcadia.recipe.craftingtable;

import com.datasiqn.arcadia.item.ArcadiaItem;
import com.datasiqn.arcadia.item.material.ArcadiaMaterial;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UltimatumRecipe implements CraftingRecipe {
    private static final ItemStack[] RECIPE = new ItemStack[9];
    private static final ArcadiaItem RESULT = new ArcadiaItem(ArcadiaMaterial.ULTIMATUM);
    private static final ItemStack ITEM_STACK_RESULT = RESULT.asCraftingResult();

    static {
        ItemStack netheriteIngot = new ArcadiaItem(Material.NETHERITE_INGOT, 16).build();

        ItemStack diamondBlock = new ArcadiaItem(Material.DIAMOND_BLOCK, 32).build();

        RECIPE[0] = netheriteIngot;
        RECIPE[1] = netheriteIngot;
        RECIPE[2] = netheriteIngot;

        RECIPE[3] = diamondBlock;
        RECIPE[4] = new ArcadiaItem(ArcadiaMaterial.ANCIENT_CORE_AWAKENED).build();
        RECIPE[5] = diamondBlock;

        // 6
        RECIPE[7] = new ArcadiaItem(ArcadiaMaterial.ENCHANTED_STICK).build();
        // 8
    }

    @Override
    public @Nullable ItemStack @NotNull [] getRecipe() {
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
