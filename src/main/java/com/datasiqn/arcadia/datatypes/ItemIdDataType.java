package com.datasiqn.arcadia.datatypes;

import com.datasiqn.arcadia.item.ItemId;
import com.datasiqn.arcadia.item.material.ArcadiaMaterial;
import org.bukkit.Material;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class ItemIdDataType implements PersistentDataType<String, ItemId> {
    @NotNull
    @Override
    public Class<String> getPrimitiveType() {
        return String.class;
    }

    @NotNull
    @Override
    public Class<ItemId> getComplexType() {
        return ItemId.class;
    }

    @NotNull
    @Override
    public String toPrimitive(@NotNull ItemId itemId, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return itemId.getStringId();
    }

    @NotNull
    @Override
    public ItemId fromPrimitive(@NotNull String s, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        try {
            return ItemId.fromArcadiaMaterial(ArcadiaMaterial.valueOf(s.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return ItemId.fromVanillaMaterial(Material.valueOf(s.toUpperCase()));
        }
    }
}
