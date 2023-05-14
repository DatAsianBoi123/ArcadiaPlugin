package com.datasiqn.arcadia.guis;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.items.ArcadiaItem;
import com.datasiqn.arcadia.recipes.ArcadiaRecipe;
import com.datasiqn.arcadia.recipes.craftingtable.CraftingRecipe;
import com.datasiqn.schedulebuilder.ScheduleBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class CraftingGUI extends ArcadiaGUI {
    private static final int[] CRAFTING_SLOTS = new int[] {10, 11, 12, 19, 20, 21, 28, 29, 30};
    private static final ItemStack MISSING_RECIPE = new ItemStack(Material.BARRIER);

    static {
        ItemMeta missingRecipeItemMeta = MISSING_RECIPE.getItemMeta();
        assert missingRecipeItemMeta != null;
        missingRecipeItemMeta.setDisplayName(ChatColor.RED + "Incorrect Recipe");
        MISSING_RECIPE.setItemMeta(missingRecipeItemMeta);
    }

    private final @Nullable ItemStack @NotNull [] craftingMatrix = new ItemStack[9];
    private final Arcadia plugin;

    @Nullable
    private CraftingRecipe currentRecipe = null;

    public CraftingGUI(Arcadia plugin) {
        super(54, "Crafting Menu");
        this.plugin = plugin;
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

        for (int y = 1; y < 4; y++) {
            for (int x = 1; x < 4; x++) {
                inv.setItem(y * 9 + x, null);
            }
        }

        updateMatrix();
        prepareCraft();
    }

    @Override
    public void clickEvent(@NotNull InventoryInteractEvent event) {
        ScheduleBuilder.create().executes(runnable -> {
            updateMatrix();
            prepareCraft();
        }).run(plugin);

        if (event instanceof InventoryClickEvent clickEvent) {
            if (clickEvent.getClickedInventory() == null) return;
            if (!(clickEvent.getClickedInventory().getHolder() instanceof CraftingGUI)) return;
            if (currentRecipe != null && clickEvent.getSlot() == 24) {
                craftItem(clickEvent);
                return;
            }
            if (Arrays.stream(CRAFTING_SLOTS).noneMatch(slot -> slot == clickEvent.getSlot())) event.setCancelled(true);
        } else if (event instanceof InventoryDragEvent dragEvent) {
            if (dragEvent.getRawSlots().stream().allMatch(slot -> slot > 54)) return;
            if (Arrays.stream(CRAFTING_SLOTS).boxed().collect(Collectors.toSet()).containsAll(dragEvent.getInventorySlots())) return;
            event.setCancelled(true);
        }
    }

    @Override
    public void closeEvent(@NotNull InventoryCloseEvent event) {
        updateMatrix();
        for (ItemStack itemStack : craftingMatrix) {
            if (itemStack == null) continue;
            HashMap<Integer, ItemStack> droppedItems = event.getPlayer().getInventory().addItem(itemStack);
            droppedItems.forEach((index, item) -> event.getPlayer().getWorld().dropItem(event.getPlayer().getEyeLocation(), item, droppedItem -> droppedItem.setVelocity(event.getPlayer().getLocation().getDirection().multiply(0.25))));
        }
    }

    private void craftItem(@NotNull InventoryClickEvent event) {
        if (currentRecipe == null) return;

        int amount = 1;
        ItemStack resultItemStack = currentRecipe.getResult().build();
        InventoryAction action = event.getAction();
        HumanEntity whoClicked = event.getWhoClicked();
        switch (action) {
            case PICKUP_ALL -> event.setCurrentItem(resultItemStack);
            case MOVE_TO_OTHER_INVENTORY -> {
                amount = 64;
                for (int i = 0; i < craftingMatrix.length; i++) {
                    if (craftingMatrix[i] == null || currentRecipe.getRecipe()[i] == null) continue;
                    int amountCanCraft = craftingMatrix[i].getAmount() / currentRecipe.getRecipe()[i].getAmount();
                    if (amountCanCraft < amount) amount = amountCanCraft;
                }
                amount = Math.min(amount, itemCanBeAdded(resultItemStack));
                event.setCancelled(true);
                if (currentRecipe.getResult().getItemData().isStackable()) {
                    resultItemStack.setAmount(amount);
                    whoClicked.getInventory().addItem(resultItemStack);
                } else {
                    for (int i = 0; i < amount; i++) {
                        whoClicked.getInventory().addItem(currentRecipe.getResult().build());
                    }
                }
                if (amount > 0) event.setCurrentItem(resultItemStack);
            }
            case SWAP_WITH_CURSOR -> {
                ItemStack itemOnCursor = whoClicked.getItemOnCursor();
                event.setCancelled(true);
                if (currentRecipe == null || !currentRecipe.getResult().isSimilar(new ArcadiaItem(itemOnCursor)) || !currentRecipe.getResult().getItemData().isStackable()) {
                    return;
                }
                itemOnCursor.setAmount(itemOnCursor.getAmount() + resultItemStack.getAmount());
            }
            default -> {
                event.setCancelled(true);
                return;
            }
        }
        if (!consumeResources(amount)) event.setCancelled(true);
        if (amount > 0) event.getWhoClicked().getWorld().playSound(event.getWhoClicked(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.7f, 2);
        ScheduleBuilder.create().executes(runnable -> {
            updateMatrix();
            prepareCraft();
        }).run(plugin);
    }

    private boolean consumeResources(int multiplier) {
        if (multiplier == 0) return true;
        if (currentRecipe == null) return false;
        for (int i = 0; i < CRAFTING_SLOTS.length; i++) {
            ItemStack item = inv.getItem(CRAFTING_SLOTS[i]);
            if (item == null || currentRecipe.getRecipe()[i] == null) continue;
            int newAmount = item.getAmount() - currentRecipe.getRecipe()[i].getAmount() * multiplier;
            if (newAmount < 0) return false;
            item.setAmount(newAmount);
        }
        return true;
    }

    private void prepareCraft() {
        ArcadiaRecipe recipe = ArcadiaRecipe.findApplicableRecipe(craftingMatrix);
        if (recipe == null) {
            inv.setItem(24, MISSING_RECIPE);
            currentRecipe = null;
        } else {
            currentRecipe = recipe.getRecipeData();
            inv.setItem(24, recipe.getRecipeData().getItemStackResult());
        }
    }

    private void updateMatrix() {
        for (int i = 0; i < CRAFTING_SLOTS.length; i++) {
            int slot = CRAFTING_SLOTS[i];
            craftingMatrix[i] = inv.getItem(slot);
        }
    }

    private int itemCanBeAdded(ItemStack itemStack) {
        int applicableSlots = 0;
        for (ItemStack item : inv.getStorageContents()) {
            if (item == null) {
                applicableSlots++;
                continue;
            }
            if (!item.isSimilar(itemStack)) continue;
            if (item.getAmount() + itemStack.getAmount() > item.getMaxStackSize()) continue;
            applicableSlots++;
        }
        return applicableSlots;
    }
}
