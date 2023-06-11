package com.datasiqn.arcadia.item.stat;

import net.md_5.bungee.api.ChatColor;

public enum ItemAttribute {
    HEALTH("Health", ChatColor.RED, StatIcon.HEALTH),
    DEFENSE("Defense", ChatColor.GREEN, StatIcon.DEFENSE),
    DAMAGE("Damage", ChatColor.RED, StatIcon.DAMAGE),
    STRENGTH("Strength", ChatColor.DARK_RED, StatIcon.STRENGTH),
    ATTACK_SPEED("Attack Speed", ChatColor.YELLOW, StatIcon.ATTACK_SPEED),
    HUNGER("Hunger", ChatColor.of("#743600"), StatIcon.HUNGER),
    ;

    private final String displayName;
    private final ChatColor color;
    private final StatIcon icon;

    ItemAttribute(String displayName, ChatColor color, StatIcon icon) {
        this.displayName = displayName;
        this.color = color;
        this.icon = icon;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public ChatColor getColor() {
        return color;
    }

    public StatIcon getIcon() {
        return icon;
    }
}
