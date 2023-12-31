package com.datasiqn.arcadia.item.material.data;

import com.datasiqn.arcadia.item.ItemRarity;
import com.datasiqn.arcadia.item.abilities.AbilityType;
import com.datasiqn.arcadia.item.abilities.ItemAbility;
import com.datasiqn.arcadia.item.components.ItemComponent;
import com.datasiqn.arcadia.item.modifiers.ItemModifier;
import com.datasiqn.arcadia.item.type.ItemType;
import com.datasiqn.arcadia.item.type.data.ExtraItemData;
import com.datasiqn.arcadia.util.lorebuilder.Lore;
import org.bukkit.Material;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public abstract class ItemBuilder<D extends ExtraItemData, V, T extends ItemBuilder<D, V, T>> {
    private final ItemType<D> type;
    private final D data;
    private final List<ItemModifier> modifiers = new ArrayList<>();
    private final Map<AbilityType, ItemAbility> abilities = new HashMap<>();
    private final List<ItemComponent> components = new ArrayList<>();

    private String name;
    private Lore lore = Lore.EMPTY;
    private Material material = Material.STONE;
    private ItemRarity rarity = ItemRarity.COMMON;
    private boolean enchantGlint;
    private boolean stackable = true;

    protected ItemBuilder(ItemType<D> type, D data) {
        if (data == null && type.requiresData()) throw new RuntimeException("item type " + type + " requires data");
        this.type = type;
        this.data = data;
    }

    public ItemType<D> itemType() {
        return type;
    }

    public @Nullable D itemData() {
        return data;
    }

    public T name(@Nullable String name) {
        this.name = name;
        return getThis();
    }

    public String name() {
        return name;
    }

    public T lore(@NotNull Lore lore) {
        this.lore = lore;
        return getThis();
    }

    public Lore lore() {
        return lore;
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

    public T addAbility(AbilityType type, @NotNull ItemAbility ability) {
        this.abilities.put(type, ability);
        return getThis();
    }

    public @UnmodifiableView Map<AbilityType, ItemAbility> abilities() {
        return Collections.unmodifiableMap(abilities);
    }

    public T addModifier(@NotNull ItemModifier modifier) {
        modifiers.add(modifier);
        return getThis();
    }

    public @UnmodifiableView List<ItemModifier> modifiers() {
        return Collections.unmodifiableList(modifiers);
    }

    public T addComponent(@NotNull ItemComponent component) {
        components.add(component);
        return getThis();
    }

    public @UnmodifiableView List<ItemComponent> components() {
        return Collections.unmodifiableList(components);
    }

    protected abstract T getThis();

    @Contract(value = " -> new", pure = true)
    public abstract @NotNull V build();
}
