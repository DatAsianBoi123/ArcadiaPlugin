package com.datasiqn.arcadia.items.types;

import com.datasiqn.arcadia.items.ItemRarity;
import com.datasiqn.arcadia.items.ItemType;
import com.datasiqn.arcadia.items.data.ItemData;
import com.datasiqn.arcadia.items.meta.MetaBuilder;
import com.datasiqn.arcadia.items.modifiers.SkullItemModifier;
import com.datasiqn.arcadia.items.stats.AttributeInstance;
import com.datasiqn.arcadia.items.stats.AttributeRange;
import com.datasiqn.arcadia.items.stats.ItemAttribute;
import com.datasiqn.arcadia.items.stats.ItemStats;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public final class ItemBerserkHelmet implements CustomMaterial {
    private final ItemData itemData = new ItemData("Berserker Helmet",
            "BERSERK_HELMET",
            Material.PLAYER_HEAD,
            ItemRarity.MYTHIC,
            false,
            false,
            null,
            ItemType.HELMET
    ).addItemModifier(new SkullItemModifier("c74f65f9b9958a6392c8b63324d76e80d2b509c1985a00232aecce409585ae2a"));

    public final MetaBuilder metaBuilder = new MetaBuilder()
            .setAttribute(ItemAttribute.DEFENSE, new AttributeRange(75, 150))
            .setAttribute(ItemAttribute.HEALTH, new AttributeRange(400, 800))
            .setAttribute(ItemAttribute.STRENGTH, new AttributeRange(250, 400));

    @Override
    public @NotNull ItemData getItemData() {
        return itemData;
    }

    @Override
    public @NotNull MetaBuilder getMetaBuilder() {
        return metaBuilder;
    }
}
