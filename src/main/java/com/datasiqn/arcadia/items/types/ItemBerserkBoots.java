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

public final class ItemBerserkBoots implements CustomMaterial {
    private final ItemData itemData = new ItemData("Berserker Boots",
            "BERSERK_BOOTS",
            Material.LEATHER_BOOTS,
            ItemRarity.MYTHIC,
            false,
            false,
            null,
            ItemType.BOOTS
    ).addItemModifier(new LeatherArmorItemModifier(Color.fromRGB(0xdb3814)));

    public final MetaBuilder metaBuilder = new MetaBuilder()
            .setAttribute(ItemAttribute.DEFENSE, new AttributeRange(50d, 100d))
            .setAttribute(ItemAttribute.HEALTH, new AttributeRange(300d, 850d))
            .setAttribute(ItemAttribute.STRENGTH, new AttributeRange(200d, 400d));

    @Override
    public @NotNull ItemData getItemData() {
        return itemData;
    }

    @Override
    public @NotNull MetaBuilder getMetaBuilder() {
        return metaBuilder;
    }
}
