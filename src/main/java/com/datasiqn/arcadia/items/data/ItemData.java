package com.datasiqn.arcadia.items.data;

import com.datasiqn.arcadia.ArcadiaKeys;
import com.datasiqn.arcadia.items.ItemRarity;
import com.datasiqn.arcadia.items.ItemType;
import com.datasiqn.arcadia.items.abilities.ItemAbility;
import com.datasiqn.arcadia.items.modifiers.ItemModifier;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemData {
    private final String name;
    private final String id;
    private final Material material;
    private final ItemRarity rarity;
    private final boolean enchantGlint;
    private final boolean stackable;
    private final @Nullable ItemAbility itemAbility;
    private final ItemType itemType;

    private final List<ItemModifier> itemModifiers = new ArrayList<>();

    public ItemData(String name, String id, Material material, ItemRarity rarity, boolean enchantGlint, boolean stackable) {
        this(name, id, material, rarity, enchantGlint, stackable, null, ItemType.NONE);
    }
    public ItemData(String name, String id, Material material, ItemRarity rarity, boolean enchantGlint, boolean stackable, @Nullable ItemAbility itemAbility, ItemType itemType) {
        this.name = name;
        this.id = id;
        this.material = material;
        this.rarity = rarity;
        this.enchantGlint = enchantGlint;
        this.stackable = stackable;
        this.itemAbility = itemAbility;
        this.itemType = itemType;
    }

    @Contract("_ -> this")
    public ItemData addItemModifier(ItemModifier modifier) {
        itemModifiers.add(modifier);
        return this;
    }

    public @Nullable String getName() {
        return name;
    }

    public @NotNull String getID() {
        return id;
    }

    public @NotNull Material getMaterial() {
        return material;
    }

    public @NotNull ItemRarity getRarity() {
        return rarity;
    }

    public boolean isEnchantGlint() {
        return enchantGlint;
    }

    public boolean isStackable() {
        return stackable;
    }

    public @Nullable ItemAbility getItemAbility() {
        return itemAbility;
    }

    public ItemType getItemType() {
        return itemType;
    }

    protected final @NotNull ItemStack toItemStack(int amount) {
        return toItemStack(amount, UUID.randomUUID());
    }
    @NotNull
    protected ItemStack toItemStack(int amount, @NotNull UUID uuid) {
        ItemStack itemStack = new ItemStack(material);
        if (stackable) itemStack.setAmount(amount);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return itemStack;
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DYE);
        meta.setUnbreakable(true);
        List<String> lore = new ArrayList<>();
        if (itemAbility != null) {
            lore.addAll(itemAbility.asLore());
            lore.add("");
        }
        lore.add(rarity + " " + itemType);
        meta.setLore(lore);
        String finalName = name == null ? WordUtils.capitalizeFully(material.toString().replaceAll("_", " ")) : name;
        meta.setDisplayName(ChatColor.RESET + "" + rarity.getColor() + finalName);
        if (enchantGlint) addEnchantGlint(meta);
        if (!stackable) addUUID(meta, uuid);
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(ArcadiaKeys.ITEM_ID, PersistentDataType.STRING, id);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public final @NotNull ItemStack buildWithModifiers(int amount, UUID uuid) {
        ItemStack itemStack = toItemStack(amount, uuid);
        itemModifiers.forEach(modifier -> {
            ItemMeta meta = modifier.modify(this, uuid, itemStack.getItemMeta());
            if (meta == null) return;
            itemStack.setItemMeta(meta);
        });
        return itemStack;
    }

    public final @NotNull ItemStack asCraftingResult(int amount, UUID uuid) {
        ItemStack itemStack = asRawCraftingResult(amount);
        itemModifiers.forEach(modifier -> {
            ItemMeta meta = modifier.modify(this, uuid, itemStack.getItemMeta());
            if (meta == null) return;
            itemStack.setItemMeta(meta);
        });
        return itemStack;
    }

    protected @NotNull ItemStack asRawCraftingResult(int amount) {
        ItemStack itemStack = toItemStack(amount);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return itemStack;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(ArcadiaKeys.CRAFTING_RESULT, PersistentDataType.BYTE, (byte) 1);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    private void addEnchantGlint(@NotNull ItemMeta meta) {
        meta.addEnchant(Enchantment.DURABILITY, 0, true);
    }

    private void addUUID(@NotNull ItemMeta meta, @NotNull UUID uuid) {
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(ArcadiaKeys.ITEM_UUID, PersistentDataType.STRING, uuid.toString());
    }
}
