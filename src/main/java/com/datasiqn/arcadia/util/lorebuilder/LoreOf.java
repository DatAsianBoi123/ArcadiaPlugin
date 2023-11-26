package com.datasiqn.arcadia.util.lorebuilder;

import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

class LoreOf implements Lore {
    private final List<String> lore;

    public LoreOf(List<String> lore) {
        this.lore = lore;
    }

    @Override
    public @Unmodifiable List<String> asStringList() {
        return lore;
    }
}
