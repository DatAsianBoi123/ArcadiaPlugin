package com.datasiqn.arcadia.util.lorebuilder;

import com.datasiqn.arcadia.util.lorebuilder.component.LoreComponent;

import java.util.ArrayList;
import java.util.List;

public class LoreBuilder {
    private final List<LoreComponent[]> lore = new ArrayList<>();

    public LoreBuilder append(LoreComponent[] component) {
        lore.add(component);
        return this;
    }

    public List<String> build() {
        return lore.stream().map(components -> {
            StringBuilder stringBuilder = new StringBuilder();
            for (LoreComponent component : components) {
                stringBuilder.append(component.toString());
            }
            return stringBuilder.toString();
        }).toList();
    }
}
