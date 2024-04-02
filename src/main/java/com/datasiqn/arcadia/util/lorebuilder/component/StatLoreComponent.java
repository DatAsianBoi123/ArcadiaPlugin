package com.datasiqn.arcadia.util.lorebuilder.component;

import com.datasiqn.arcadia.player.AttributeFormat;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class StatLoreComponent implements LoreComponent {
    private final AttributeFormat attributeFormat;
    private final double value;

    private StatLoreComponent(AttributeFormat attributeFormat, double value) {
        this.attributeFormat = attributeFormat;
        this.value = value;
    }

    @Override
    public @NotNull String toString() {
        return attributeFormat.format(value);
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull StatLoreComponent stat(AttributeFormat attribute, double value) {
        return new StatLoreComponent(attribute, value);
    }
}
