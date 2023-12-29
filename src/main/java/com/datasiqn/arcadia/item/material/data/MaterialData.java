package com.datasiqn.arcadia.item.material.data;

import com.datasiqn.arcadia.ArcadiaTag;
import com.datasiqn.arcadia.item.ItemRarity;
import com.datasiqn.arcadia.item.abilities.AbilityType;
import com.datasiqn.arcadia.item.abilities.ItemAbility;
import com.datasiqn.arcadia.item.components.ItemComponent;
import com.datasiqn.arcadia.item.modifiers.ItemModifier;
import com.datasiqn.arcadia.item.type.ItemType;
import com.datasiqn.arcadia.item.type.data.ExtraItemData;
import com.datasiqn.arcadia.util.PdcUtil;
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
    private final @NotNull ItemType<D> itemType;
    private final D itemData;
    private final @NotNull List<ItemModifier> itemModifiers;

    private final @Nullable String name;
    private final @NotNull Material material;
    private final @NotNull ItemRarity rarity;
    private final boolean enchantGlint;
    private final boolean stackable;
    private final Map<AbilityType, ItemAbility> itemAbilities;
    private final List<ItemComponent> itemComponents;

    @Contract(pure = true)
    public MaterialData(@NotNull ItemBuilder<D, ?, ?> builder) {
        itemType = builder.itemType();
        itemData = builder.itemData();
        itemModifiers = builder.modifiers();

        name = builder.name();
        material = builder.material();
        rarity = builder.rarity();
        enchantGlint = builder.enchantGlint();
        stackable = builder.stackable();
        itemAbilities = builder.abilities();
        itemComponents = builder.components();
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

    @NotNull
    @UnmodifiableView
    public Map<AbilityType, ItemAbility> getItemAbilities() {
        return itemAbilities;
    }

    @NotNull
    @UnmodifiableView
    public List<ItemComponent> getItemComponents() {
        return itemComponents;
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
