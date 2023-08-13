package com.datasiqn.arcadia.item.type;

import com.datasiqn.arcadia.item.type.data.ConsumableData;
import com.datasiqn.arcadia.item.type.data.ExtraItemData;
import com.datasiqn.arcadia.item.type.data.NoneItemData;
import com.datasiqn.arcadia.player.PlayerData;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ItemType<D extends ExtraItemData> {
    ItemType<@Nullable NoneItemData> NONE = new CustomItemType<>(EquipmentSlot.HAND, "", false);

    ItemType<@Nullable NoneItemData> SWORD = new CustomItemType<>(EquipmentSlot.HAND, "SWORD", false, 1.6);

    ItemType<@Nullable NoneItemData> BOW = new CustomItemType<>(EquipmentSlot.HAND, "BOW", false);

    ItemType<@Nullable NoneItemData> HELMET = new CustomItemType<>(EquipmentSlot.HEAD, "HELMET", false);

    ItemType<@Nullable NoneItemData> CHESTPLATE = new CustomItemType<>(EquipmentSlot.CHEST, "CHESTPLATE", false);

    ItemType<@Nullable NoneItemData> LEGGINGS = new CustomItemType<>(EquipmentSlot.LEGS, "LEGGINGS", false);

    ItemType<@Nullable NoneItemData> BOOTS = new CustomItemType<>(EquipmentSlot.FEET, "BOOTS", false);

    ItemType<@NotNull ConsumableData> CONSUMABLE = new CustomItemType<>(EquipmentSlot.HAND, "CONSUMABLE", true);

    ItemType<@Nullable NoneItemData> POWER_STONE = new CustomItemType<>(null, "POWER STONE", false);

    @Nullable
    EquipmentSlot getSlot();

    double getAttackSpeed();

    @Override
    String toString();

    boolean requiresData();

    final class CustomItemType<D extends ExtraItemData> implements ItemType<D>  {
        private final EquipmentSlot slot;
        private final String displayName;
        private final boolean requiresData;
        private final double attackSpeed;

        CustomItemType(EquipmentSlot slot, String displayName, boolean requiresData) {
            this(slot, displayName, requiresData, PlayerData.DEFAULT_ATTACK_SPEED);
        }
        CustomItemType(EquipmentSlot slot, String displayName, boolean requiresData, double attackSpeed) {
            this.slot = slot;
            this.displayName = displayName;
            this.requiresData = requiresData;
            this.attackSpeed = attackSpeed;
        }

        @Override
        public @Nullable EquipmentSlot getSlot() {
            return slot;
        }

        @Override
        public double getAttackSpeed() {
            return attackSpeed;
        }

        @Override
        public String toString() {
            return displayName;
        }

        @Override
        public boolean requiresData() {
            return requiresData;
        }
    }
}
