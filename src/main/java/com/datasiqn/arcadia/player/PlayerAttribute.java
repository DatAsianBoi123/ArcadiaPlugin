package com.datasiqn.arcadia.player;

import com.datasiqn.arcadia.item.stat.StatIcon;
import net.md_5.bungee.api.ChatColor;

public enum PlayerAttribute {
    MAX_HEALTH("Health", ChatColor.RED, StatIcon.HEALTH, 10),
    DEFENSE("Defense", ChatColor.GREEN, StatIcon.DEFENSE, 0),
    STRENGTH("Strength", ChatColor.DARK_RED, StatIcon.STRENGTH, 0),
    ATTACK_SPEED("Attack Speed", ChatColor.YELLOW, StatIcon.ATTACK_SPEED, 0),
    MAX_HUNGER("Max Hunger", ChatColor.of("#743600"), StatIcon.HUNGER, 100),
    SPEED("Speed", ChatColor.WHITE, StatIcon.SPEED, 1),
    ;

    private final String displayName;
    private final double defaultValue;
    private final ChatColor color;
    private final String icon;

    PlayerAttribute(String displayName, ChatColor color, String icon, double defaultValue) {
        this.displayName = displayName;
        this.color = color;
        this.icon = icon;
        this.defaultValue = defaultValue;
    }

    public ChatColor getColor() {
        return color;
    }

    public String getIcon() {
        return icon;
    }

    public double getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
