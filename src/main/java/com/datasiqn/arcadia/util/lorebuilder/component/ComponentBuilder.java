package com.datasiqn.arcadia.util.lorebuilder.component;

import com.datasiqn.arcadia.items.stats.ItemAttribute;
import org.bukkit.ChatColor;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class ComponentBuilder {
    private final List<LoreComponent> components = new ArrayList<>();

    public ComponentBuilder text(String text) {
        return append(TextLoreComponent.text(text));
    }
    public ComponentBuilder text(String text, ChatColor color) {
        return append(TextLoreComponent.text(text, color));
    }

    public ComponentBuilder attribute(ItemAttribute attribute) {
        return append(AttributeLoreComponent.attribute(attribute));
    }

    public ComponentBuilder stat(double value, ItemAttribute attribute) {
        return append(StatLoreComponent.stat(value, attribute));
    }

    public ComponentBuilder number(Number num) {
        return append(NumberComponent.number(num));
    }
    public ComponentBuilder number(Number num, NumberFormat format) {
        return append(NumberComponent.number(num, format));
    }

    public ComponentBuilder percent(double percent) {
        return append(PercentageComponent.percent(percent));
    }
    public ComponentBuilder percent(double percent, DecimalFormat format) {
        return append(PercentageComponent.percent(percent, format));
    }

    public ComponentBuilder append(LoreComponent component) {
        this.components.add(component);
        return this;
    }

    public LoreComponent[] build() {
        return components.toArray(LoreComponent[]::new);
    }
}
