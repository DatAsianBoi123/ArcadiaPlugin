package com.datasiqn.arcadia.recipe.craftingtable;

import com.datasiqn.arcadia.item.ArcadiaItem;
import com.datasiqn.arcadia.item.material.ArcadiaMaterial;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AwakenedCoreRecipe implements CraftingRecipe {
    private static final ItemStack[] RECIPE = new ItemStack[9];
    private static final ArcadiaItem RESULT = new ArcadiaItem(ArcadiaMaterial.ANCIENT_CORE_AWAKENED);
    private static final ItemStack ITEM_STACK_RESULT = RESULT.asCraftingResult();

    static {
        ItemStack amethystShard = new ArcadiaItem(Material.AMETHYST_SHARD, 64).build();

        // 0
        RECIPE[1] = new ArcadiaItem(Material.NETHERITE_BLOCK, 8).build();
        // 2

        RECIPE[3] = amethystShard;
        RECIPE[4] = new ArcadiaItem(ArcadiaMaterial.ANCIENT_CORE).build();
        RECIPE[5] = amethystShard;

        // 6
        RECIPE[7] = new ArcadiaItem(ArcadiaMaterial.ESSENCE_OF_BOB).build();
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
