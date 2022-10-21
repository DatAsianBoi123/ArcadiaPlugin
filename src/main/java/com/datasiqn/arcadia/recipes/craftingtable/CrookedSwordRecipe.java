package com.datasiqn.arcadia.recipes.craftingtable;

import com.datasiqn.arcadia.items.ArcadiaItem;
import com.datasiqn.arcadia.items.materials.ArcadiaMaterial;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrookedSwordRecipe implements CraftingRecipe {
    private static final ItemStack[] RECIPE = new ItemStack[9];
    private static final ArcadiaItem RESULT = new ArcadiaItem(ArcadiaMaterial.CROOKED_SWORD);
    private static final ItemStack ITEM_STACK_RESULT = RESULT.asCraftingResult();

    static {
        ItemStack oakLog = ArcadiaItem.from(Material.OAK_LOG, 10);
        // 0
        // 1
        RECIPE[2] = oakLog;

        // 3
        RECIPE[4] = oakLog;
        // 5

        RECIPE[6] = ArcadiaItem.from(Material.STICK, 1);
        // 7
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
