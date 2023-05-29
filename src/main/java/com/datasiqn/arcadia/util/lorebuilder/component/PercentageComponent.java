package com.datasiqn.arcadia.util.lorebuilder.component;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class PercentageComponent implements LoreComponent {
    private static final DecimalFormat DEFAULT_FORMAT;
    static {
        DEFAULT_FORMAT = new DecimalFormat("0");
        DEFAULT_FORMAT.setMaximumFractionDigits(340); // 340 is max fraction digits
    }

    private final double percent;
    private final DecimalFormat format;

    private PercentageComponent(double percent, DecimalFormat format) {
        this.percent = percent;
        this.format = format;
    }

    @Override
    public @NotNull String toString() {
        String num = format == null ? String.valueOf(percent * 100) : format.format(percent * 100);
        return ChatColor.BLUE + num + "%";
    }

    @Contract("_ -> new")
    public static @NotNull PercentageComponent percent(double percent) {
        return percent(percent, DEFAULT_FORMAT);
    }
    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull PercentageComponent percent(double percent, DecimalFormat format) {
        return new PercentageComponent(percent, format);
    }
}
