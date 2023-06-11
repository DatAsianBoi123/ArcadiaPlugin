package com.datasiqn.arcadia.item.type;

import com.datasiqn.arcadia.item.type.data.ConsumableData;
import com.datasiqn.arcadia.item.type.data.ExtraItemData;
import com.datasiqn.arcadia.item.type.data.NoneItemData;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ItemType<D extends ExtraItemData> {
    ItemType<@Nullable NoneItemData> NONE = new CustomItemType<>(EquipmentSlot.HAND, "", false);

    ItemType<@Nullable NoneItemData> SWORD = new CustomItemType<>(EquipmentSlot.HAND, "SWORD", false);

    ItemType<@Nullable NoneItemData> BOW = new CustomItemType<>(EquipmentSlot.HAND, "BOW", false);

    ItemType<@Nullable NoneItemData> HELMET = new CustomItemType<>(EquipmentSlot.HEAD, "HELMET", false);

    ItemType<@Nullable NoneItemData> CHESTPLATE = new CustomItemType<>(EquipmentSlot.CHEST, "CHESTPLATE", false);

    ItemType<@Nullable NoneItemData> LEGGINGS = new CustomItemType<>(EquipmentSlot.LEGS, "LEGGINGS", false);

    ItemType<@Nullable NoneItemData> BOOTS = new CustomItemType<>(EquipmentSlot.FEET, "BOOTS", false);

    ItemType<@NotNull ConsumableData> CONSUMABLE = new CustomItemType<>(EquipmentSlot.HAND, "CONSUMABLE", true);

    ItemType<@Nullable NoneItemData> POWER_STONE = new CustomItemType<>(null, "POWER STONE", false);

    @Nullable
    EquipmentSlot getSlot();

    @Override
    String toString();

    boolean requiresData();

    final class CustomItemType<D extends ExtraItemData> implements ItemType<D>  {
        private final EquipmentSlot slot;
        private final String displayName;
        private final boolean requiresData;

        CustomItemType(EquipmentSlot slot, String displayName, boolean requiresData) {
            this.slot = slot;
            this.displayName = displayName;
            this.requiresData = requiresData;
        }

        @Override
        public @Nullable EquipmentSlot getSlot() {
            return slot;
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
