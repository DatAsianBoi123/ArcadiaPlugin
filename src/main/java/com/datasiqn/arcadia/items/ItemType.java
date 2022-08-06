package com.datasiqn.arcadia.items;

import org.bukkit.inventory.EquipmentSlot;

public enum ItemType {
    NONE(EquipmentSlot.HAND, ""),
    SWORD(EquipmentSlot.HAND, "SWORD"),
    BOW(EquipmentSlot.HAND, "BOW"),
    HELMET(EquipmentSlot.HEAD, "HELMET"),
    CHESTPLATE(EquipmentSlot.CHEST, "CHESTPLATE"),
    LEGGINGS(EquipmentSlot.LEGS, "LEGGINGS"),
    BOOTS(EquipmentSlot.FEET, "BOOTS");

    private final EquipmentSlot slot;
    private final String displayName;

    ItemType(EquipmentSlot slot, String displayName) {
        this.slot = slot;
        this.displayName = displayName;
    }

    public EquipmentSlot getSlot() {
        return slot;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
