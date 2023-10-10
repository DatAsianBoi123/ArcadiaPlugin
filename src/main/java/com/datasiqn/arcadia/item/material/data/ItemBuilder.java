package com.datasiqn.arcadia.item.material.data;

import com.datasiqn.arcadia.item.ItemRarity;
import com.datasiqn.arcadia.item.abilities.AbilityType;
import com.datasiqn.arcadia.item.abilities.ItemAbility;
import com.datasiqn.arcadia.item.modifiers.ItemModifier;
import com.datasiqn.arcadia.item.type.ItemType;
import com.datasiqn.arcadia.item.type.data.ExtraItemData;
import org.bukkit.Material;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public abstract class ItemBuilder<D extends ExtraItemData, V, T extends ItemBuilder<D, V, T>> {
    private final ItemType<D> itemType;
    private final D itemData;
    private final List<ItemModifier> itemModifiers = new ArrayList<>();
    private final Map<AbilityType, ItemAbility> itemAbilities = new HashMap<>();

    private String name;
    private Material material = Material.STONE;
    private ItemRarity rarity = ItemRarity.COMMON;
    private boolean enchantGlint;
    private boolean stackable = true;

    protected ItemBuilder(ItemType<D> itemType, D itemData) {
        if (itemData == null && itemType.requiresData()) throw new RuntimeException("item type " + itemType + " requires data");
        this.itemType = itemType;
        this.itemData = itemData;
    }

    public ItemType<D> itemType() {
        return itemType;
    }

    public @Nullable D itemData() {
        return itemData;
    }

    public T name(@Nullable String name) {
        this.name = name;
        return getThis();
    }

    public String name() {
        return name;
    }

    public T material(@NotNull Material material) {
        this.material = material;
        return getThis();
    }

    public Material material() {
        return material;
    }

    public T rarity(@NotNull ItemRarity rarity) {
        this.rarity = rarity;
        return getThis();
    }

    public ItemRarity rarity() {
        return rarity;
    }

    public T enchantGlint(boolean enchantGlint) {
        this.enchantGlint = enchantGlint;
        return getThis();
    }

    public boolean enchantGlint() {
        return enchantGlint;
    }

    public T stackable(boolean stackable) {
        this.stackable = stackable;
        return getThis();
    }

    public boolean stackable() {
        return stackable;
    }

    public T addAbility(AbilityType type, @NotNull ItemAbility itemAbility) {
        this.itemAbilities.put(type, itemAbility);
        return getThis();
    }

    public @UnmodifiableView Map<AbilityType, ItemAbility> abilities() {
        return Collections.unmodifiableMap(itemAbilities);
    }

    public T addModifier(@NotNull ItemModifier modifier) {
        itemModifiers.add(modifier);
        return getThis();
    }

    public @UnmodifiableView List<ItemModifier> modifiers() {
        return Collections.unmodifiableList(itemModifiers);
    }

    protected abstract T getThis();

    @Contract(value = " -> new", pure = true)
    public abstract @NotNull V build();
}
