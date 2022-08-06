package com.datasiqn.arcadia.items.types;

import com.datasiqn.arcadia.items.ItemRarity;
import com.datasiqn.arcadia.items.ItemType;
import com.datasiqn.arcadia.items.data.ItemData;
import com.datasiqn.arcadia.items.meta.MetaBuilder;
import com.datasiqn.arcadia.items.stats.AttributeInstance;
import com.datasiqn.arcadia.items.stats.ItemAttribute;
import com.datasiqn.arcadia.items.stats.ItemStats;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public final class ItemBow implements CustomMaterial {
    private final ItemData itemData = new ItemData("Bow",
            "BOW",
            Material.BOW,
            ItemRarity.COMMON,
            false,
            false,
            null,
            ItemType.BOW);

    private final MetaBuilder metaBuilder = new MetaBuilder()
            .setAttribute(ItemAttribute.DAMAGE, 5);

    @Override
    public @NotNull ItemData getItemData() {
        return itemData;
    }

    @Override
    public @NotNull MetaBuilder getMetaBuilder() {
        return metaBuilder;
    }
}
