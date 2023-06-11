package com.datasiqn.arcadia.item.type.data;

import com.datasiqn.arcadia.util.lorebuilder.Lore;
import org.jetbrains.annotations.NotNull;

public interface ExtraItemData {
    default @NotNull Lore getLore() {
        return Lore.of();
    }
}
