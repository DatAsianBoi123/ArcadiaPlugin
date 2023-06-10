package com.datasiqn.arcadia.items.material.data;

import com.datasiqn.arcadia.ArcadiaTag;
import com.datasiqn.arcadia.items.ItemRarity;
import com.datasiqn.arcadia.items.abilities.AbilityType;
import com.datasiqn.arcadia.items.abilities.ItemAbility;
import com.datasiqn.arcadia.items.modifiers.ItemModifier;
import com.datasiqn.arcadia.items.type.ItemType;
import com.datasiqn.arcadia.items.type.data.ExtraItemData;
import com.datasiqn.arcadia.util.PdcUtil;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.*;

public class MaterialData<D extends ExtraItemData> {
    private final @NotNull ItemType<D> itemType;
    private final D itemData;
    private final @NotNull List<ItemModifier> itemModifiers;

    private final @Nullable String name;
    private final @NotNull Material material;
    private final @NotNull ItemRarity rarity;
    private final boolean enchantGlint;
    private final boolean stackable;
    private final Map<AbilityType, ItemAbility> itemAbilities;

    @Contract(pure = true)
    public MaterialData(@NotNull Builder<D> builder) {
        itemType = builder.itemType;
        itemData = builder.itemData;
        itemModifiers = builder.itemModifiers;

        name = builder.name;
        material = builder.material;
        rarity = builder.rarity;
        enchantGlint = builder.enchantGlint;
        stackable = builder.stackable;
        itemAbilities = builder.itemAbilities;
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

    public @NotNull Material getMaterial() {
        return material;
    }

    public @NotNull ItemRarity getRarity() {
        return rarity;
    }

    public boolean hasEnchantGlint() {
        return enchantGlint;
    }

    public boolean isStackable() {
        return stackable;
    }

    public @NotNull Map<AbilityType, ItemAbility> getItemAbilities() {
        return new HashMap<>(itemAbilities);
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
        if (!itemAbilities.isEmpty()) {
            for (Map.Entry<AbilityType, ItemAbility> entry : itemAbilities.entrySet()) {
                AbilityType type = entry.getKey();
                ItemAbility ability = entry.getValue();
                lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "Item Ability: " + ChatColor.WHITE + ability.getName() + " " + type);
                ability.getDescription().addTo(lore);
                DecimalFormat decimalFormat = new DecimalFormat("#.#");
                lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: " + ChatColor.GREEN + decimalFormat.format(ability.getCooldown() / 20) + "s");
                lore.add("");
            }
        }
        if (itemData != null) {
            itemData.getLore().addTo(lore);
            lore.add("");
        }
        lore.add(rarity + " " + itemType);
        meta.setLore(lore);
        String finalName = name == null ? WordUtils.capitalizeFully(material.toString().replaceAll("_", " ")) : name;
        meta.setDisplayName(ChatColor.RESET + "" + rarity.getColor() + finalName);
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        if (enchantGlint) addEnchantGlint(meta);
        if (!stackable) PdcUtil.set(pdc, ArcadiaTag.ITEM_UUID, uuid);

        itemModifiers.forEach(modifier -> modifier.modify(uuid, meta));

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public @NotNull ItemStack asCraftingResult(int amount, @NotNull UUID uuid) {
        ItemStack itemStack = toItemStack(amount, uuid);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return itemStack;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        PdcUtil.set(pdc, ArcadiaTag.CRAFTING_RESULT, true);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    private void addEnchantGlint(@NotNull ItemMeta meta) {
        meta.addEnchant(Enchantment.DURABILITY, 0, true);
    }

    public static final class Builder<D extends ExtraItemData> {
        private final ItemType<D> itemType;
        private final D itemData;
        private final List<ItemModifier> itemModifiers = new ArrayList<>();
        private final Map<AbilityType, ItemAbility> itemAbilities = new HashMap<>();

        private String name;
        private Material material = Material.STONE;
        private ItemRarity rarity = ItemRarity.COMMON;
        private boolean enchantGlint;
        private boolean stackable = true;

        public Builder(ItemType<D> itemType) {
            this(itemType, null);
        }
        public Builder(ItemType<D> itemType, D itemData) {
            if (itemData == null && itemType.requiresData()) throw new RuntimeException("item type " + itemType + " requires data");
            this.itemType = itemType;
            this.itemData = itemData;
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

        public Builder<D> addAbility(AbilityType type, @NotNull ItemAbility itemAbility) {
            this.itemAbilities.put(type, itemAbility);
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
