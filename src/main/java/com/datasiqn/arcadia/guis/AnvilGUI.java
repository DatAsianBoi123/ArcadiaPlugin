package com.datasiqn.arcadia.guis;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaKeys;
import com.datasiqn.arcadia.datatype.ArcadiaDataType;
import com.datasiqn.arcadia.enchants.EnchantType;
import com.datasiqn.arcadia.items.ArcadiaItem;
import com.datasiqn.arcadia.items.materials.ArcadiaMaterial;
import com.datasiqn.arcadia.items.meta.ArcadiaItemMeta;
import com.datasiqn.arcadia.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class AnvilGUI extends ArcadiaGUI {
    private static final ItemStack MISSING_RECIPE = new ItemStack(Material.BARRIER);
    private static final ItemStack CORRECT_SLOT = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
    private static final ItemStack INCORRECT_SLOT = new ItemStack(Material.RED_STAINED_GLASS_PANE);

    static {
        ItemMeta missingRecipeItemMeta = MISSING_RECIPE.getItemMeta();
        assert missingRecipeItemMeta != null;
        missingRecipeItemMeta.setDisplayName(ChatColor.RED + "Incorrect Recipe");
        MISSING_RECIPE.setItemMeta(missingRecipeItemMeta);

        ItemMeta slotItemMeta = CORRECT_SLOT.getItemMeta();
        assert slotItemMeta != null;
        slotItemMeta.setDisplayName(" ");
        CORRECT_SLOT.setItemMeta(slotItemMeta);
        INCORRECT_SLOT.setItemMeta(slotItemMeta);
    }

    private final Arcadia plugin;

    private ItemStack originalItem;
    private ItemStack addedItem;
    private ItemStack result;

    public AnvilGUI(Arcadia plugin) {
        super(54, "Anvil");
        this.plugin = plugin;
        init();
    }

    public void init() {
        ItemStack emptyItem = ItemUtil.createEmpty(Material.GRAY_STAINED_GLASS_PANE);
        for (int i = 0; i < 54; i++) {
            this.inv.setItem(i, emptyItem);
        }

        this.inv.setItem(28, null);
        this.inv.setItem(34, null);

        update();
    }

    @Override
    public void clickEvent(@NotNull InventoryInteractEvent event) {
        plugin.runAfterOneTick(this::update);

        if (event instanceof InventoryClickEvent clickEvent) {
            if (clickEvent.getClickedInventory() == null) return;
            if (!(clickEvent.getClickedInventory().getHolder() instanceof AnvilGUI)) return;
            if (clickEvent.getSlot() == 28 || clickEvent.getSlot() == 34) return;
            if (clickEvent.getSlot() == 31 && result != null) {
                inv.setItem(31, new ArcadiaItem(result).build());
                inv.setItem(28, null);
                inv.setItem(34, null);
                clickEvent.getWhoClicked().getWorld().playSound(clickEvent.getWhoClicked(), Sound.BLOCK_ANVIL_USE, 1, 1);
                return;
            }
            event.setCancelled(true);
        } else if (event instanceof InventoryDragEvent) {
            event.setCancelled(true);
        }

        event.setCancelled(true);
    }

    @Override
    public void closeEvent(@NotNull InventoryCloseEvent event) {
        update();

        if (originalItem != null && !event.getPlayer().getInventory().addItem(originalItem).isEmpty()) {
            event.getPlayer().getWorld().dropItem(event.getPlayer().getEyeLocation(), originalItem, droppedItem -> droppedItem.setVelocity(event.getPlayer().getLocation().getDirection().multiply(0.25)));
        }

        if (addedItem != null && !event.getPlayer().getInventory().addItem(addedItem).isEmpty()) {
            event.getPlayer().getWorld().dropItem(event.getPlayer().getEyeLocation(), addedItem, droppedItem -> droppedItem.setVelocity(event.getPlayer().getLocation().getDirection().multiply(0.25)));
        }
    }

    private void update() {
        originalItem = inv.getItem(28);
        addedItem = inv.getItem(34);

        prepareAnvilCraft();

        boolean leftCorrect = originalItem != null && (addedItem == null || result != null);

        boolean rightCorrect = addedItem != null && (originalItem == null || result != null);

        inv.setItem(29, leftCorrect ? CORRECT_SLOT : INCORRECT_SLOT);
        inv.setItem(30, leftCorrect ? CORRECT_SLOT : INCORRECT_SLOT);

        inv.setItem(32, rightCorrect ? CORRECT_SLOT : INCORRECT_SLOT);
        inv.setItem(33, rightCorrect ? CORRECT_SLOT : INCORRECT_SLOT);
    }

    private void prepareAnvilCraft() {
        result = null;
        if (addedItem == null || originalItem == null) {
            inv.setItem(31, MISSING_RECIPE);
            return;
        }
        ArcadiaItem originalArcadiaItem = new ArcadiaItem(originalItem);
        ArcadiaItem addedArcadiaItem = new ArcadiaItem(addedItem);

        ArcadiaItemMeta originalMeta = originalArcadiaItem.getItemMeta();
        ArcadiaItemMeta addedMeta = addedArcadiaItem.getItemMeta();
        if (addedArcadiaItem.getMaterial() == ArcadiaMaterial.ENCHANTED_BOOK || originalArcadiaItem.isSimilar(addedArcadiaItem)) {
            boolean canAddEnchants = !originalArcadiaItem.getItemMeta().hasEnchants() && !addedArcadiaItem.getItemMeta().hasEnchants();
            for (Map.Entry<EnchantType, Integer> enchant : addedMeta.getEnchants().entrySet()) {
                EnchantType enchantType = enchant.getKey();
                Integer level = enchant.getValue();
                boolean canEnchant = enchantType.getEnchantment().canEnchant(originalArcadiaItem);
                if (canEnchant) canAddEnchants = true;
                int originalEnchant = originalMeta.getEnchantLevel(enchantType);
                int combinedLevel = Math.max(level, originalEnchant);
                if (level == originalEnchant) combinedLevel++;
                if (originalArcadiaItem.getMaterial() == ArcadiaMaterial.ENCHANTED_BOOK || canEnchant) originalMeta.addEnchant(enchantType, combinedLevel);
            }

            if (!canAddEnchants) {
                inv.setItem(31, MISSING_RECIPE);
                return;
            }

            if (originalArcadiaItem.isSimilar(addedArcadiaItem)) {
                double newBonus = originalMeta.getItemQualityBonus() + addedMeta.getItemQuality() + addedMeta.getItemQualityBonus();
                originalMeta.setItemQualityBonus(newBonus);
            }
        }

        if (addedArcadiaItem.getMaterial() == ArcadiaMaterial.SPACE_REWRITER) {
            originalMeta.setItemQualityBonus(originalMeta.getItemQualityBonus() + 0.1);
        }

        inv.setItem(31, MISSING_RECIPE);

        ItemStack result = originalArcadiaItem.build();
        if (result.equals(originalItem)) return;
        ItemMeta meta = result.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(ArcadiaKeys.ANVIL_RESULT, ArcadiaDataType.BOOLEAN, true);

        result.setItemMeta(meta);

        inv.setItem(31, result);
        this.result = result;
    }
}
