package com.datasiqn.arcadia.util.lorebuilder.component;

import com.datasiqn.arcadia.player.PlayerAttribute;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class StatLoreComponent implements LoreComponent {
    private final PlayerAttribute attribute;
    private final double value;

    private StatLoreComponent(PlayerAttribute attribute, double value) {
        this.attribute = attribute;
        this.value = value;
    }

    @Override
    public @NotNull String toString() {
        return attribute.getColor() + String.valueOf(value) + attribute.getIcon();
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull StatLoreComponent stat(PlayerAttribute attribute, double value) {
        return new StatLoreComponent(attribute, value);
    }
}
