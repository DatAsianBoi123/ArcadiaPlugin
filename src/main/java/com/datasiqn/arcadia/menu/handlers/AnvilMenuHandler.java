package com.datasiqn.arcadia.menu.handlers;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaTag;
import com.datasiqn.arcadia.item.ArcadiaItem;
import com.datasiqn.arcadia.item.material.ArcadiaMaterial;
import com.datasiqn.arcadia.item.meta.ArcadiaItemMeta;
import com.datasiqn.arcadia.util.ItemUtil;
import com.datasiqn.arcadia.util.PdcUtil;
import com.datasiqn.menuapi.inventory.MenuHandler;
import com.datasiqn.menuapi.inventory.item.StaticMenuItem;
import com.datasiqn.schedulebuilder.ScheduleBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

public class AnvilMenuHandler extends MenuHandler {
    private static final ItemStack MISSING_RECIPE = new ItemStack(Material.BARRIER);
    private static final ItemStack CORRECT_SLOT = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
    private static final ItemStack INCORRECT_SLOT = new ItemStack(Material.RED_STAINED_GLASS_PANE);

    static {
        ItemMeta missingRecipeItemMeta = MISSING_RECIPE.getItemMeta();
        if (missingRecipeItemMeta != null) {
            missingRecipeItemMeta.setDisplayName(ChatColor.RED + "Incorrect Recipe");
            MISSING_RECIPE.setItemMeta(missingRecipeItemMeta);
        }

        ItemMeta slotItemMeta = CORRECT_SLOT.getItemMeta();
        if (slotItemMeta != null) {
            slotItemMeta.setDisplayName(" ");
            CORRECT_SLOT.setItemMeta(slotItemMeta);
            INCORRECT_SLOT.setItemMeta(slotItemMeta);
        }
    }

    private final Arcadia plugin;

    private ItemStack originalItem;
    private ItemStack addedItem;
    private ItemStack result;

    public AnvilMenuHandler(Arcadia plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        super.onClick(event);

        ScheduleBuilder.create().executes(runnable -> update(event.getInventory())).run(plugin);

        Inventory inv = event.getInventory();
        if (event.getClickedInventory() == event.getView().getBottomInventory()) return;
        if (event.getSlot() == 28 || event.getSlot() == 34) return;
        if (event.getSlot() == 31 && result != null) {
            inv.setItem(31, new ArcadiaItem(result).build());
            inv.setItem(28, null);
            inv.setItem(34, null);
            event.getWhoClicked().getWorld().playSound(event.getWhoClicked(), Sound.BLOCK_ANVIL_USE, 1, 1);
            return;
        }
        event.setCancelled(true);
    }

    @Override
    public void onDrag(@NotNull InventoryDragEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {
        super.onOpen(event);

        update(event.getInventory());
    }

    @Override
    public void onClose(@NotNull InventoryCloseEvent event) {
        super.onClose(event);

        update(event.getInventory());

        Inventory inventory = event.getPlayer().getInventory();
        if (originalItem != null && !inventory.addItem(originalItem).isEmpty()) {
            event.getPlayer().getWorld().dropItem(event.getPlayer().getEyeLocation(), originalItem, droppedItem -> droppedItem.setVelocity(event.getPlayer().getLocation().getDirection().multiply(0.25)));
        }

        if (addedItem != null && !inventory.addItem(addedItem).isEmpty()) {
            event.getPlayer().getWorld().dropItem(event.getPlayer().getEyeLocation(), addedItem, droppedItem -> droppedItem.setVelocity(event.getPlayer().getLocation().getDirection().multiply(0.25)));
        }
    }

    private void update(@NotNull Inventory inventory) {
        originalItem = inventory.getItem(28);
        addedItem = inventory.getItem(34);

        prepareAnvilCraft(inventory);

        boolean leftCorrect = originalItem != null && (addedItem == null || result != null);

        boolean rightCorrect = addedItem != null && (originalItem == null || result != null);

        inventory.setItem(29, leftCorrect ? CORRECT_SLOT : INCORRECT_SLOT);
        inventory.setItem(30, leftCorrect ? CORRECT_SLOT : INCORRECT_SLOT);

        inventory.setItem(32, rightCorrect ? CORRECT_SLOT : INCORRECT_SLOT);
        inventory.setItem(33, rightCorrect ? CORRECT_SLOT : INCORRECT_SLOT);
    }

    private void prepareAnvilCraft(Inventory inventory) {
        result = null;
        if (addedItem == null || originalItem == null) {
            inventory.setItem(31, MISSING_RECIPE);
            return;
        }
        ArcadiaItem originalArcadiaItem = new ArcadiaItem(originalItem);
        ArcadiaItem addedArcadiaItem = new ArcadiaItem(addedItem);

        ArcadiaItemMeta originalMeta = originalArcadiaItem.getItemMeta();
        ArcadiaItemMeta addedMeta = addedArcadiaItem.getItemMeta();
        if (addedArcadiaItem.getMaterial() == ArcadiaMaterial.ENCHANTED_BOOK || originalArcadiaItem.isSimilar(addedArcadiaItem)) {
            double newBonus = originalMeta.getItemQuality() + addedMeta.getItemQuality();
            originalMeta.setItemQuality(newBonus);
        }

        if (addedArcadiaItem.getMaterial() == ArcadiaMaterial.SPACE_REWRITER) {
            originalMeta.setItemQuality(originalMeta.getItemQuality() + 0.1);
        }

        inventory.setItem(31, MISSING_RECIPE);

        ItemStack result = originalArcadiaItem.build();
        if (result.equals(originalItem)) return;
        ItemMeta meta = result.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        PdcUtil.set(pdc, ArcadiaTag.ANVIL_RESULT, true);

        result.setItemMeta(meta);

        inventory.setItem(31, result);
        this.result = result;
    }

    @Override
    public void populate(HumanEntity humanEntity, Inventory inventory) {
        ItemStack emptyItem = ItemUtil.createEmpty(Material.GRAY_STAINED_GLASS_PANE);
        for (int i = 0; i < 54; i++) {
            if (i == 28 || i == 34 || i == 31) continue;
            setItem(i, new StaticMenuItem(emptyItem));
        }
    }

    @Override
    public @NotNull Inventory createInventory() {
        return Bukkit.createInventory(null, 54, "Anvil");
    }
}
