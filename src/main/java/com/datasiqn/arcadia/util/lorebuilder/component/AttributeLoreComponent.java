package com.datasiqn.arcadia.util.lorebuilder.component;

import com.datasiqn.arcadia.player.PlayerAttribute;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class AttributeLoreComponent implements LoreComponent {
    private final PlayerAttribute attribute;

    private AttributeLoreComponent(PlayerAttribute attribute) {
        this.attribute = attribute;
    }

    @Override
    public @NotNull String toString() {
        return attribute.getColor() + attribute.toString() + attribute.getIcon();
    }

    @Contract("_ -> new")
    public static @NotNull AttributeLoreComponent attribute(PlayerAttribute attribute) {
        return new AttributeLoreComponent(attribute);
    }
}
