package com.datasiqn.arcadia.util.lorebuilder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;

class EmptyLore implements Lore {
    @Override
    public @Unmodifiable List<String> asStringList() {
        return Collections.emptyList();
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public void addTo(@NotNull List<String> list) { }

    @Override
    public void addTo(int index, @NotNull List<String> list) { }
}
