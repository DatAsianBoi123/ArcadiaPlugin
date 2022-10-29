package com.datasiqn.arcadia.items.materials.data;

import com.datasiqn.arcadia.ArcadiaKeys;
import com.datasiqn.arcadia.datatype.ArcadiaDataType;
import com.datasiqn.arcadia.items.type.ItemType;
import com.datasiqn.arcadia.items.type.data.NoneItemData;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class DefaultMaterialData extends MaterialData<@Nullable NoneItemData> {
    public DefaultMaterialData(@NotNull Material material) {
        super(new Builder<>(ItemType.NONE, material.name()).material(material));
    }

    @Override
    public @NotNull ItemStack toItemStack(int amount, @NotNull UUID uuid) {
        ItemStack itemStack = super.toItemStack(amount, uuid);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return itemStack;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(ArcadiaKeys.ITEM_MATERIAL, ArcadiaDataType.BOOLEAN, true);
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
