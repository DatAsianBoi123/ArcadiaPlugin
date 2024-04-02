package com.datasiqn.arcadia.player;

import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public final class AttributeFormat {
    public static final DecimalFormat FORMAT = new DecimalFormat("#,###.#");

    private final String displayName;
    private final ChatColor color;
    private final String icon;
    private final boolean sign;

    private AttributeFormat(String displayName, ChatColor color, String icon, boolean sign) {
        this.displayName = displayName;
        this.color = color;
        this.icon = icon;
        this.sign = sign;
    }

    public @NotNull String format(double value) {
        String num = "";
        if (sign) {
            num = value < 0 ? ChatColor.RED + "-" : ChatColor.GRAY + "+";
            value = Math.abs(value);
        }
        num += color + FORMAT.format(value);
        return num + icon;
    }

    public String displayName() {
        return displayName;
    }

    public ChatColor color() {
        return color;
    }

    public String icon() {
        return icon;
    }

    public boolean displaySign() {
        return sign;
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    public static @NotNull AttributeFormat withoutSign(String displayName, ChatColor color, String icon) {
        return new AttributeFormat(displayName, color, icon, false);
    }

    public static @NotNull AttributeFormat withSign(String displayName, ChatColor color, String icon) {
        return new AttributeFormat(displayName, color, icon, true);
    }
}
