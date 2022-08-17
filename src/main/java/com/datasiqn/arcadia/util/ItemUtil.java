package com.datasiqn.arcadia.util;

import com.datasiqn.arcadia.ArcadiaKeys;
import com.datasiqn.arcadia.items.data.ItemData;
import com.datasiqn.arcadia.items.data.MaterialItemData;
import com.datasiqn.arcadia.items.types.ArcadiaMaterial;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ItemUtil {
    private ItemUtil() {}

    @Contract("_ -> new")
    public static @NotNull ItemData fromDefaultItem(Material type) {
        return new MaterialItemData(type);
    }

    public static @Nullable ArcadiaMaterial getFrom(@NotNull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return null;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (!pdc.has(ArcadiaKeys.ITEM_ID, PersistentDataType.STRING)) return null;
        if (pdc.has(ArcadiaKeys.ITEM_MATERIAL, PersistentDataType.BYTE)) {
            Byte bool = pdc.get(ArcadiaKeys.ITEM_MATERIAL, PersistentDataType.BYTE);
            assert bool != null;
            if (bool == (byte) 1) return null;
        }

        String id = pdc.get(ArcadiaKeys.ITEM_ID, PersistentDataType.STRING);
        return ArcadiaMaterial.valueOf(id);
    }
}
