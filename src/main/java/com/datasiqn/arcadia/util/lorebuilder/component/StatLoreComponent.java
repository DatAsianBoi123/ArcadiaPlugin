package com.datasiqn.arcadia.util.lorebuilder.component;

import com.datasiqn.arcadia.item.stat.ItemAttribute;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class StatLoreComponent implements LoreComponent {
    private final ItemAttribute attribute;
    private final double value;

    private StatLoreComponent(double value, ItemAttribute attribute) {
        this.attribute = attribute;
        this.value = value;
    }

    @Override
    public @NotNull String toString() {
        return attribute.getColor() + String.valueOf(value) + attribute.getIcon();
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull StatLoreComponent stat(double value, ItemAttribute attribute) {
        return new StatLoreComponent(value, attribute);
    }
}
