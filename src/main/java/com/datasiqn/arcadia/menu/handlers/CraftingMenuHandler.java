package com.datasiqn.arcadia.menu.handlers;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.item.ArcadiaItem;
import com.datasiqn.arcadia.recipe.ArcadiaRecipe;
import com.datasiqn.arcadia.recipe.craftingtable.CraftingRecipe;
import com.datasiqn.arcadia.util.ItemUtil;
import com.datasiqn.menuapi.inventory.MenuHandler;
import com.datasiqn.menuapi.inventory.item.StaticMenuItem;
import com.datasiqn.schedulebuilder.ScheduleBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class CraftingMenuHandler extends MenuHandler {
    private static final int[] CRAFTING_SLOTS = new int[] {10, 11, 12, 19, 20, 21, 28, 29, 30};
    private static final ItemStack MISSING_RECIPE = new ItemStack(Material.BARRIER);

    static {
        ItemMeta meta = MISSING_RECIPE.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RED + "Incorrect Recipe");
            MISSING_RECIPE.setItemMeta(meta);
        }
    }

    private final @Nullable ItemStack @NotNull [] craftingMatrix = new ItemStack[9];
    private final Arcadia plugin;

    @Nullable
    private CraftingRecipe currentRecipe = null;

    public CraftingMenuHandler(Arcadia plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        super.onClick(event);

        ScheduleBuilder.create().executes(runnable -> {
            Inventory inventory = event.getInventory();
            updateMatrix(inventory);
            prepareCraft(inventory);
        }).run(plugin);

        if (event.getClickedInventory() == event.getView().getBottomInventory()) return;
        if (currentRecipe != null && event.getSlot() == 24) {
            craftItem(event);
            return;
        }
        if (Arrays.stream(CRAFTING_SLOTS).noneMatch(slot -> slot == event.getSlot())) event.setCancelled(true);
    }

    @Override
    public void onDrag(@NotNull InventoryDragEvent event) {
        if (event.getRawSlots().stream().allMatch(slot -> slot > 54)) return;
        if (Arrays.stream(CRAFTING_SLOTS).boxed().collect(Collectors.toSet()).containsAll(event.getInventorySlots())) return;
        event.setCancelled(true);
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {
        super.onOpen(event);

        Inventory inventory = event.getInventory();
        updateMatrix(inventory);
        prepareCraft(inventory);
    }

    @Override
    public void onClose(@NotNull InventoryCloseEvent event) {
        updateMatrix(event.getInventory());

        for (ItemStack itemStack : craftingMatrix) {
            if (itemStack == null) continue;
            HumanEntity player = event.getPlayer();
            HashMap<Integer, ItemStack> droppedItems = player.getInventory().addItem(itemStack);
            droppedItems.forEach((index, item) -> player.getWorld().dropItem(player.getEyeLocation(), item, droppedItem -> droppedItem.setVelocity(player.getLocation().getDirection().multiply(0.25))));
        }
    }

    private void craftItem(@NotNull InventoryClickEvent event) {
        if (currentRecipe == null) return;

        int amount = 1;
        ItemStack resultItemStack = currentRecipe.getResult().build();
        InventoryAction action = event.getAction();
        HumanEntity whoClicked = event.getWhoClicked();
        Inventory inventory = event.getInventory();
        switch (action) {
            case PICKUP_ALL -> event.setCurrentItem(resultItemStack);
            case MOVE_TO_OTHER_INVENTORY -> {
                amount = 64;
                for (int i = 0; i < craftingMatrix.length; i++) {
                    if (craftingMatrix[i] == null || currentRecipe.getRecipe()[i] == null) continue;
                    int amountCanCraft = craftingMatrix[i].getAmount() / currentRecipe.getRecipe()[i].getAmount();
                    if (amountCanCraft < amount) amount = amountCanCraft;
                }
                amount = Math.min(amount, itemCanBeAdded(resultItemStack, whoClicked.getInventory()));
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
        if (!consumeResources(amount, inventory)) event.setCancelled(true);
        if (amount > 0) event.getWhoClicked().getWorld().playSound(event.getWhoClicked(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.7f, 2);
        ScheduleBuilder.create().executes(runnable -> {
            updateMatrix(inventory);
            prepareCraft(inventory);
        }).run(plugin);
    }

    private boolean consumeResources(int multiplier, Inventory inventory) {
        if (multiplier == 0) return true;
        if (currentRecipe == null) return false;
        for (int i = 0; i < CRAFTING_SLOTS.length; i++) {
            ItemStack item = inventory.getItem(CRAFTING_SLOTS[i]);
            if (item == null || currentRecipe.getRecipe()[i] == null) continue;
            int newAmount = item.getAmount() - currentRecipe.getRecipe()[i].getAmount() * multiplier;
            if (newAmount < 0) return false;
            item.setAmount(newAmount);
        }
        return true;
    }

    private void prepareCraft(Inventory inventory) {
        ArcadiaRecipe recipe = ArcadiaRecipe.findApplicableRecipe(craftingMatrix);
        if (recipe == null) {
            inventory.setItem(24, MISSING_RECIPE);
            currentRecipe = null;
        } else {
            currentRecipe = recipe.getRecipeData();
            inventory.setItem(24, recipe.getRecipeData().getItemStackResult());
        }
    }

    private void updateMatrix(Inventory inventory) {
        for (int i = 0; i < CRAFTING_SLOTS.length; i++) {
            int slot = CRAFTING_SLOTS[i];
            craftingMatrix[i] = inventory.getItem(slot);
        }
    }

    private int itemCanBeAdded(ItemStack itemStack, @NotNull Inventory inventory) {
        int applicableSlots = 0;
        for (ItemStack item : inventory.getStorageContents()) {
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

    @Override
    public void populate(HumanEntity humanEntity) {
        ItemStack emptyItem = ItemUtil.createEmpty(Material.GRAY_STAINED_GLASS_PANE);
        for (int i = 0; i < 54; i++) {
            if (i == 24) continue;
            setItem(i, new StaticMenuItem(emptyItem));
        }

        for (int y = 1; y < 4; y++) {
            for (int x = 1; x < 4; x++) {
                removeItem(y * 9 + x);
            }
        }
    }

    @Override
    public @NotNull Inventory createInventory() {
        return Bukkit.createInventory(null, 54, "Crafting Menu");
    }
}
