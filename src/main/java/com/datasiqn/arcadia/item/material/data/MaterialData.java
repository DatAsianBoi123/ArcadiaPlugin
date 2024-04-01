package com.datasiqn.arcadia.item.material.data;

import com.datasiqn.arcadia.ArcadiaTag;
import com.datasiqn.arcadia.item.ItemRarity;
import com.datasiqn.arcadia.item.abilities.AbilityType;
import com.datasiqn.arcadia.item.abilities.ItemAbility;
import com.datasiqn.arcadia.item.components.ItemComponent;
import com.datasiqn.arcadia.item.modifiers.ItemModifier;
import com.datasiqn.arcadia.item.stat.AttributeRange;
import com.datasiqn.arcadia.item.stat.ItemStats;
import com.datasiqn.arcadia.item.type.ItemType;
import com.datasiqn.arcadia.item.type.data.ExtraItemData;
import com.datasiqn.arcadia.util.PdcUtil;
import com.datasiqn.arcadia.util.lorebuilder.Lore;
import net.minecraft.world.entity.EquipmentSlot;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MaterialData<D extends ExtraItemData> {
    private final @NotNull ItemType<D> type;
    private final D data;
    private final @NotNull List<ItemModifier> modifiers;

    private final @Nullable String name;
    private final @NotNull Lore lore;
    private final @NotNull Material material;
    private final @NotNull ItemRarity rarity;
    private final AttributeRange damage;
    private final boolean enchantGlint;
    private final boolean stackable;
    private final ItemStats stats;
    private final Map<AbilityType, ItemAbility> abilities;
    private final List<ItemComponent> components;

    @Contract(pure = true)
    public MaterialData(@NotNull ItemBuilder<D, ?, ?> builder) {
        type = builder.itemType();
        data = builder.itemData();
        modifiers = builder.modifiers();

        name = builder.name();
        lore = builder.lore();
        material = builder.material();
        rarity = builder.rarity();
        damage = builder.damage();
        enchantGlint = builder.enchantGlint();
        stackable = builder.stackable();
        stats = builder.stats();
        abilities = builder.abilities();
        components = builder.components();
    }

    public @NotNull ItemType<D> getType() {
        return type;
    }

    public D getData() {
        return data;
    }

    public @Nullable String getName() {
        return name;
    }

    public AttributeRange getDamage() {
        return damage;
    }

    public ItemStats getStats() {
        return stats;
    }

    public @NotNull Lore getLore() {
        return lore;
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

    @NotNull
    @UnmodifiableView
    public Map<AbilityType, ItemAbility> getAbilities() {
        return abilities;
    }

    @NotNull
    @UnmodifiableView
    public List<ItemComponent> getComponents() {
        return components;
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
        if (!this.lore.isEmpty()) {
            this.lore.addTo(lore);
            lore.add("");
        }
        if (!abilities.isEmpty()) {
            for (Map.Entry<AbilityType, ItemAbility> entry : abilities.entrySet()) {
                AbilityType type = entry.getKey();
                ItemAbility ability = entry.getValue();
                lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "Item Ability: " + ChatColor.WHITE + ability.getName() + " " + type);
                ability.getDescription().addTo(lore);
                DecimalFormat decimalFormat = new DecimalFormat("#.#");
                lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: " + ChatColor.GREEN + decimalFormat.format(ability.getCooldown() / 20) + "s");
                lore.add("");
            }
        }
        if (data != null) {
            data.getLore().addTo(lore);
            lore.add("");
        }
        lore.add(rarity + " " + type);
        meta.setLore(lore);
        String finalName = name == null ? WordUtils.capitalizeFully(material.toString().replaceAll("_", " ")) : name;
        meta.setDisplayName(ChatColor.RESET + "" + rarity.getColor() + finalName);
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        if (enchantGlint) addEnchantGlint(meta);
        if (!stackable) PdcUtil.set(pdc, ArcadiaTag.ITEM_UUID, uuid);

        modifiers.forEach(modifier -> modifier.modify(uuid, meta));

        if (!CraftItemStack.asNMSCopy(itemStack).getItem().getDefaultAttributeModifiers(EquipmentSlot.MAINHAND).isEmpty()) {
            // removes the default attack speed of weapons
            UUID modifierUuid = stackable ? new UUID(0, 0) : uuid;
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(modifierUuid, "generic.attack_speed", 0, AttributeModifier.Operation.ADD_NUMBER));
        }

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

    @Contract("_ -> new")
    public static <D extends ExtraItemData> @NotNull Builder<D> builder(ItemType<D> type) {
        return builder(type, null);
    }
    @Contract("_, _ -> new")
    public static <D extends ExtraItemData> @NotNull Builder<D> builder(ItemType<D> type, D itemData) {
        return new Builder<>(type, itemData);
    }

    public static class Builder<D extends ExtraItemData> extends ItemBuilder<D, MaterialData<D>, Builder<D>> {
        protected Builder(ItemType<D> itemType, D itemData) {
            super(itemType, itemData);
        }

        @Override
        protected Builder<D> getThis() {
            return this;
        }

        @Contract(value = " -> new", pure = true)
        public @NotNull MaterialData<D> build() {
            return new MaterialData<>(this);
        }
    }
}
