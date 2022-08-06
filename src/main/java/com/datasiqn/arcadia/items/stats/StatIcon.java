package com.datasiqn.arcadia.items.stats;

public enum StatIcon {
    DAMAGE("✧"),
    DEFENSE("❉"),
    HEALTH("♥"),
    STRENGTH("⁂"),
    ATTACK_SPEED("⚔");

    private final String icon;

    StatIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return icon;
    }
}
