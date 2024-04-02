package com.datasiqn.arcadia.util.lorebuilder.component;

import com.datasiqn.arcadia.player.AttributeFormat;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class AttributeLoreComponent implements LoreComponent {
    private final AttributeFormat attributeFormat;

    private AttributeLoreComponent(AttributeFormat attributeFormat) {
        this.attributeFormat = attributeFormat;
    }

    @Override
    public @NotNull String toString() {
        return attributeFormat.color() + attributeFormat.displayName() + attributeFormat.icon();
    }

    @Contract("_ -> new")
    public static @NotNull AttributeLoreComponent attribute(AttributeFormat attribute) {
        return new AttributeLoreComponent(attribute);
    }
}
