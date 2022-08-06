package com.datasiqn.arcadia.items.data;

import com.datasiqn.arcadia.ArcadiaKeys;
import com.datasiqn.arcadia.items.ItemRarity;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class MaterialItemData extends ItemData {
    public MaterialItemData(Material material) {
        super(null, material.name(), material, ItemRarity.COMMON, false, true);
    }

    @Override
    public @NotNull ItemStack toItemStack(int amount, @NotNull UUID uuid) {
        ItemStack itemStack = super.toItemStack(amount, uuid);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return itemStack;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(ArcadiaKeys.ITEM_MATERIAL, PersistentDataType.BYTE, (byte) 1);
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
