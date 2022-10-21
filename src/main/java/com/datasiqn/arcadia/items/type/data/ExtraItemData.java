package com.datasiqn.arcadia.items.type.data;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface ExtraItemData {
    default @NotNull List<String> getLore() {
        return new ArrayList<>();
    }
}
