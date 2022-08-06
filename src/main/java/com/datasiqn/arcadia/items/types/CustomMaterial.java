package com.datasiqn.arcadia.items.types;

import com.datasiqn.arcadia.items.data.ItemData;
import com.datasiqn.arcadia.items.meta.MetaBuilder;
import org.jetbrains.annotations.NotNull;

public interface CustomMaterial {
    @NotNull ItemData getItemData();

    @NotNull MetaBuilder getMetaBuilder();
}
