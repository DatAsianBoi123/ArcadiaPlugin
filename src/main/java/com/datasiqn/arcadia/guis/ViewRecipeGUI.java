package com.datasiqn.arcadia.guis;

import com.datasiqn.arcadia.recipes.ArcadiaRecipe;
import com.datasiqn.arcadia.recipes.craftingtable.CraftingRecipe;
import com.datasiqn.arcadia.util.ItemUtil;
import com.datasiqn.menuapi.inventory.MenuHandler;
import com.datasiqn.menuapi.inventory.item.StaticMenuItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ViewRecipeGUI extends MenuHandler {
    private static final int[] CRAFTING_SLOTS = new int[] {10, 11, 12, 19, 20, 21, 28, 29, 30};
    private final ArcadiaRecipe recipe;

    public ViewRecipeGUI(@NotNull ArcadiaRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        super.onClick(event);

        event.setCancelled(true);
    }

    @Override
    public void populate(HumanEntity humanEntity) {
        ItemStack emptyItem = ItemUtil.createEmpty(Material.GRAY_STAINED_GLASS_PANE);
        for (int i = 0; i < 54; i++) {
            setItem(i, new StaticMenuItem(emptyItem));
        }

        CraftingRecipe recipeData = recipe.getRecipeData();
        ItemStack[] recipeArray = recipeData.getRecipe();
        for (int i = 0; i < CRAFTING_SLOTS.length; i++) {
            if (recipeArray[i] == null) removeItem(CRAFTING_SLOTS[i]);
            else setItem(CRAFTING_SLOTS[i], new StaticMenuItem(recipeArray[i]));
        }

        setItem(24, new StaticMenuItem(recipeData.getItemStackResult()));
    }

    @Override
    public @NotNull Inventory createInventory() {
        return Bukkit.createInventory(null, 54, recipe.name().toLowerCase());
    }
}
