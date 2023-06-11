package com.datasiqn.arcadia.item.stat;

public enum StatIcon {
    DAMAGE("✧"),
    DEFENSE("❉"),
    HEALTH("♥"),
    STRENGTH("⁂"),
    ATTACK_SPEED("⚔"),
    HUNGER("🍖")
    ;

    private final String icon;

    StatIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return icon;
    }
}
