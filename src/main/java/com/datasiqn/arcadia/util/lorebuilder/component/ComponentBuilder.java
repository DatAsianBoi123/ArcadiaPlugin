package com.datasiqn.arcadia.util.lorebuilder.component;

import com.datasiqn.arcadia.items.stats.ItemAttribute;

import java.util.ArrayList;
import java.util.List;

public class ComponentBuilder {
    private final List<LoreComponent> components = new ArrayList<>();

    public ComponentBuilder text(String text) {
        this.components.add(TextLoreComponent.text(text));
        return this;
    }

    public ComponentBuilder attribute(ItemAttribute attribute) {
        this.components.add(AttributeLoreComponent.attribute(attribute));
        return this;
    }

    public ComponentBuilder append(LoreComponent component) {
        this.components.add(component);
        return this;
    }

    public LoreComponent[] build() {
        return components.toArray(LoreComponent[]::new);
    }
}
