package com.datasiqn.arcadia.item.material.data;

import com.datasiqn.arcadia.item.ItemRarity;
import com.datasiqn.arcadia.item.abilities.AbilityType;
import com.datasiqn.arcadia.item.abilities.ItemAbility;
import com.datasiqn.arcadia.item.components.ItemComponent;
import com.datasiqn.arcadia.item.modifiers.ItemModifier;
import com.datasiqn.arcadia.item.stat.AttributeRange;
import com.datasiqn.arcadia.item.stat.ItemStats;
import com.datasiqn.arcadia.item.type.ItemType;
import com.datasiqn.arcadia.item.type.data.ExtraItemData;
import com.datasiqn.arcadia.player.PlayerAttribute;
import com.datasiqn.arcadia.util.lorebuilder.Lore;
import org.bukkit.Material;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

// TODO: redo item builders
public abstract class ItemBuilder<D extends ExtraItemData, V, T extends ItemBuilder<D, V, T>> {
    private final ItemType<D> type;
    private final D data;
    private final ItemStats stats = new ItemStats();
    private final List<ItemModifier> modifiers = new ArrayList<>();
    private final Map<AbilityType, ItemAbility> abilities = new HashMap<>();
    private final List<ItemComponent> components = new ArrayList<>();

    private String name;
    private Lore lore = Lore.EMPTY;
    private Material material = Material.STONE;
    private AttributeRange damage = new AttributeRange(0, 0);
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

    public T damage(double damage) {
        return damage(new AttributeRange(damage, damage));
    }
    public T damage(double min, double max) {
        return damage(new AttributeRange(min, max));
    }
    public T damage(AttributeRange damage) {
        this.damage = damage;
        return getThis();
    }

    public AttributeRange damage() {
        return damage;
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

    public T attribute(PlayerAttribute attribute, double value) {
        return attribute(attribute, new AttributeRange(value, value));
    }
    public T attribute(PlayerAttribute attribute, double min, double max) {
        return attribute(attribute, new AttributeRange(min, max));
    }
    public T attribute(PlayerAttribute attribute, AttributeRange range) {
        this.stats.setAttribute(attribute, range);
        return getThis();
    }

    public ItemStats stats() {
        return stats;
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
