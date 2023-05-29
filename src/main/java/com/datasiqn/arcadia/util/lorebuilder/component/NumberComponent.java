package com.datasiqn.arcadia.util.lorebuilder.component;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;

public class NumberComponent implements LoreComponent {
    private final Number value;
    private final NumberFormat format;

    private NumberComponent(Number value, NumberFormat format) {
        this.value = value;
        this.format = format;
    }

    @Override
    public @NotNull String toString() {
        String num = format == null ? value.toString() : format.format(value);
        return ChatColor.GREEN + num;
    }

    public static @NotNull NumberComponent number(@NotNull Number value) {
        return number(value, null);
    }
    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull NumberComponent number(@NotNull Number value, NumberFormat format) {
        return new NumberComponent(value, format);
    }
}
