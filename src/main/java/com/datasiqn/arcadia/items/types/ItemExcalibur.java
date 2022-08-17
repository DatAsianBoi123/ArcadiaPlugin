package com.datasiqn.arcadia.items.types;

import com.datasiqn.arcadia.items.ItemRarity;
import com.datasiqn.arcadia.items.ItemType;
import com.datasiqn.arcadia.items.data.ItemData;
import com.datasiqn.arcadia.items.meta.MetaBuilder;
import com.datasiqn.arcadia.items.stats.AttributeRange;
import com.datasiqn.arcadia.items.stats.ItemAttribute;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public final class ItemExcalibur implements CustomMaterial {
    private final ItemData itemData = new ItemData("Excalibur",
            "EXCALIBUR",
            Material.GOLDEN_SWORD,
            ItemRarity.LEGENDARY,
            true,
            false,
            null,
            ItemType.SWORD);

    private final MetaBuilder metaBuilder = new MetaBuilder()
            .setAttribute(ItemAttribute.DAMAGE, new AttributeRange(2000, 3500))
            .setAttribute(ItemAttribute.DEFENSE, 200)
            .setAttribute(ItemAttribute.ATTACK_SPEED, 100);

    @Override
    public @NotNull ItemData getItemData() {
        return itemData;
    }

    @NotNull
    @Override
    public MetaBuilder getMetaBuilder() {
        return metaBuilder;
    }
}
