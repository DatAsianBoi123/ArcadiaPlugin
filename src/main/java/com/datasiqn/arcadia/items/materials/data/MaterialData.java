package com.datasiqn.arcadia.items.materials.data;

import com.datasiqn.arcadia.ArcadiaKeys;
import com.datasiqn.arcadia.items.ItemRarity;
import com.datasiqn.arcadia.items.abilities.ItemAbility;
import com.datasiqn.arcadia.items.modifiers.ItemModifier;
import com.datasiqn.arcadia.items.type.ItemType;
import com.datasiqn.arcadia.items.type.data.ExtraItemData;
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

public class MaterialData<D extends ExtraItemData> {
    private final @NotNull ItemType<D> itemType;
    private final D itemData;
    private final @NotNull String id;
    private final @NotNull List<ItemModifier> itemModifiers;

    private final @Nullable String name;
    private final @NotNull Material material;
    private final @NotNull ItemRarity rarity;
    private final boolean enchantGlint;
    private final boolean stackable;
    private final @Nullable ItemAbility itemAbility;

    @Contract(pure = true)
    public MaterialData(@NotNull Builder<D> builder) {
        itemType = builder.itemType;
        itemData = builder.itemData;
        id = builder.id;
        itemModifiers = builder.itemModifiers;

        name = builder.name;
        material = builder.material;
        rarity = builder.rarity;
        enchantGlint = builder.enchantGlint;
        stackable = builder.stackable;
        itemAbility = builder.itemAbility;
    }

    public @NotNull ItemType<D> getItemType() {
        return itemType;
    }

    public D getData() {
        return itemData;
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

    public @NotNull ItemStack toItemStack() {
        return toItemStack(1, UUID.randomUUID());
    }
    public @NotNull ItemStack toItemStack(int amount, @NotNull UUID uuid) {
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
        if (itemData != null) {
            lore.addAll(itemData.getLore());
            lore.add("");
        }
        lore.add(rarity + " " + itemType);
        meta.setLore(lore);
        String finalName = name == null ? WordUtils.capitalizeFully(material.toString().replaceAll("_", " ")) : name;
        meta.setDisplayName(ChatColor.RESET + "" + rarity.getColor() + finalName);
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(ArcadiaKeys.ITEM_ID, PersistentDataType.STRING, id);

        if (enchantGlint) addEnchantGlint(meta);
        if (!stackable) pdc.set(ArcadiaKeys.ITEM_UUID, PersistentDataType.STRING, uuid.toString());

        itemModifiers.forEach(modifier -> modifier.modify(uuid, meta));

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public @NotNull ItemStack asCraftingResult(int amount, @NotNull UUID uuid) {
        ItemStack itemStack = toItemStack(amount, uuid);
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

    public static final class Builder<D extends ExtraItemData> {
        private final ItemType<D> itemType;
        private final D itemData;
        private final String id;
        private final List<ItemModifier> itemModifiers = new ArrayList<>();

        private String name;
        private Material material = Material.STONE;
        private ItemRarity rarity = ItemRarity.COMMON;
        private boolean enchantGlint;
        private boolean stackable = true;
        private ItemAbility itemAbility;

        public Builder(ItemType<D> itemType, @NotNull String id) {
            this(itemType, id, null);
        }
        public Builder(ItemType<D> itemType, @NotNull String id, D itemData) {
            if (itemData == null && itemType.requiresData()) throw new RuntimeException("item type " + itemType + " requires data");
            this.itemType = itemType;
            this.itemData = itemData;
            this.id = id;
        }

        public Builder<D> name(@Nullable String name) {
            this.name = name;
            return this;
        }

        public Builder<D> material(@NotNull Material material) {
            this.material = material;
            return this;
        }

        public Builder<D> rarity(@NotNull ItemRarity rarity) {
            this.rarity = rarity;
            return this;
        }

        public Builder<D> enchantGlint(boolean enchantGlint) {
            this.enchantGlint = enchantGlint;
            return this;
        }

        public Builder<D> stackable(boolean stackable) {
            this.stackable = stackable;
            return this;
        }

        public Builder<D> itemAbility(@Nullable ItemAbility itemAbility) {
            this.itemAbility = itemAbility;
            return this;
        }

        public Builder<D> addModifier(@NotNull ItemModifier modifier) {
            itemModifiers.add(modifier);
            return this;
        }

        @Contract(value = " -> new", pure = true)
        public @NotNull MaterialData<D> build() {
            return new MaterialData<>(this);
        }
    }
}
