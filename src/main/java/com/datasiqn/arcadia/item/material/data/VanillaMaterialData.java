package com.datasiqn.arcadia.item.material.data;

import com.datasiqn.arcadia.ArcadiaTag;
import com.datasiqn.arcadia.item.type.ItemType;
import com.datasiqn.arcadia.item.type.data.NoneItemData;
import com.datasiqn.arcadia.util.PdcUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class VanillaMaterialData extends MaterialData<@Nullable NoneItemData> {
    public VanillaMaterialData(@NotNull Material material) {
        super(builder(ItemType.NONE).material(material));
    }

    @Override
    public @NotNull ItemStack toItemStack(int amount, @NotNull UUID uuid) {
        ItemStack itemStack = super.toItemStack(amount, uuid);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return itemStack;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        PdcUtil.set(pdc, ArcadiaTag.ITEM_MATERIAL, true);
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
