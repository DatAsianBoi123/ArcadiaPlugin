package com.datasiqn.arcadia.players;

import com.datasiqn.arcadia.items.ArcadiaItem;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class PlayerEquipment {
    private static final ArcadiaItem EMPTY_ITEM = new ArcadiaItem(Material.AIR);

    private final Map<EquipmentSlot, ArcadiaItem> equipment = new HashMap<>();

    private final ArcadiaItem[] amulet = new ArcadiaItem[9];

    public void setItem(EquipmentSlot equipmentSlot, @NotNull ArcadiaItem item) {
        equipment.put(equipmentSlot, item);
    }

    @NotNull
    public ArcadiaItem getItem(EquipmentSlot equipmentSlot) {
        return equipment.getOrDefault(equipmentSlot, EMPTY_ITEM);
    }

    public void setItemInMainHand(@NotNull ArcadiaItem item) {
        setItem(EquipmentSlot.HAND, item);
    }

    @NotNull
    public ArcadiaItem getItemInMainHand() {
        return getItem(EquipmentSlot.HAND);
    }

    public void setHelmet(@NotNull ArcadiaItem item) {
        setItem(EquipmentSlot.HEAD, item);
    }

    @NotNull
    public ArcadiaItem getHelmet() {
        return getItem(EquipmentSlot.HEAD);
    }

    public void setChestplate(@NotNull ArcadiaItem item) {
        setItem(EquipmentSlot.CHEST, item);
    }

    @NotNull
    public ArcadiaItem getChestplate() {
        return getItem(EquipmentSlot.CHEST);
    }

    public void setLeggings(@NotNull ArcadiaItem item) {
        setItem(EquipmentSlot.LEGS, item);
    }

    @NotNull
    public ArcadiaItem getLeggings() {
        return getItem(EquipmentSlot.LEGS);
    }

    public void setBoots(@NotNull ArcadiaItem item) {
        setItem(EquipmentSlot.FEET, item);
    }

    @NotNull
    public ArcadiaItem getBoots() {
        return getItem(EquipmentSlot.FEET);
    }

    public @Nullable ArcadiaItem @NotNull [] getAmulet() {
        return amulet;
    }
}
