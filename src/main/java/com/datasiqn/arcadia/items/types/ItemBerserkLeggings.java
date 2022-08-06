package com.datasiqn.arcadia.items.types;

import com.datasiqn.arcadia.items.ItemRarity;
import com.datasiqn.arcadia.items.ItemType;
import com.datasiqn.arcadia.items.data.ItemData;
import com.datasiqn.arcadia.items.meta.MetaBuilder;
import com.datasiqn.arcadia.items.modifiers.LeatherArmorItemModifier;
import com.datasiqn.arcadia.items.stats.AttributeInstance;
import com.datasiqn.arcadia.items.stats.AttributeRange;
import com.datasiqn.arcadia.items.stats.ItemAttribute;
import com.datasiqn.arcadia.items.stats.ItemStats;
import org.bukkit.Color;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public final class ItemBerserkLeggings implements CustomMaterial {
    private final ItemData itemData = new ItemData("Berserker Leggings",
            "BERSERK_LEGGINGS",
            Material.LEATHER_LEGGINGS,
            ItemRarity.MYTHIC,
            false,
            false,
            null,
            ItemType.LEGGINGS
    ).addItemModifier(new LeatherArmorItemModifier(Color.fromRGB(0xdb3814)));

    public final MetaBuilder metaBuilder = new MetaBuilder()
            .setAttribute(ItemAttribute.DEFENSE, new AttributeRange(100, 175))
            .setAttribute(ItemAttribute.HEALTH, new AttributeRange(450, 900))
            .setAttribute(ItemAttribute.STRENGTH, new AttributeRange(250, 400));

    @NotNull
    @Override
    public ItemData getItemData() {
        return itemData;
    }

    @Override
    public @NotNull MetaBuilder getMetaBuilder() {
        return metaBuilder;
    }
}
