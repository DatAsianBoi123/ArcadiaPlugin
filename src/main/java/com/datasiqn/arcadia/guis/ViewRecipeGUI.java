package com.datasiqn.arcadia.guis;

import com.datasiqn.arcadia.recipes.ArcadiaRecipe;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class ViewRecipeGUI extends ArcadiaGUI {
    private static final int[] CRAFTING_SLOTS = new int[] {10, 11, 12, 19, 20, 21, 28, 29, 30};
    private final ArcadiaRecipe recipe;

    public ViewRecipeGUI(@NotNull ArcadiaRecipe recipe) {
        super(54, recipe.name().toLowerCase(Locale.ROOT));
        this.recipe = recipe;
        init();
    }

    public void init() {
        ItemStack emptyItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = emptyItem.getItemMeta();
        assert meta != null;
        meta.setDisplayName(" ");
        emptyItem.setItemMeta(meta);
        for (int i = 0; i < 54; i++) {
            inv.setItem(i, emptyItem);
        }

        for (int i = 0; i < CRAFTING_SLOTS.length; i++) {
            inv.setItem(CRAFTING_SLOTS[i], recipe.getRecipeData().getRecipe()[i]);
        }

        inv.setItem(24, recipe.getRecipeData().getItemStackResult());
    }

    @Override
    public void clickEvent(@NotNull InventoryInteractEvent event) {
        event.setCancelled(true);
    }
}
