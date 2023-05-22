package com.datasiqn.arcadia.util.lorebuilder.component;

import com.datasiqn.arcadia.items.stats.ItemAttribute;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class AttributeLoreComponent implements LoreComponent {
    private final ItemAttribute attribute;

    private AttributeLoreComponent(ItemAttribute attribute) {
        this.attribute = attribute;
    }

    @Override
    public @NotNull String toString() {
        return attribute.getColor() + attribute.toString() + attribute.getIcon();
    }

    @Contract("_ -> new")
    public static @NotNull AttributeLoreComponent attribute(ItemAttribute attribute) {
        return new AttributeLoreComponent(attribute);
    }
}
