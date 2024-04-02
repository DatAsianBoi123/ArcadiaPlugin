package com.datasiqn.arcadia.player;

public enum PlayerAttribute {
    MAX_HEALTH(AttributeFormats.HEALTH, 10),
    DEFENSE(AttributeFormats.DEFENSE, 0),
    STRENGTH(AttributeFormats.STRENGTH, 0),
    ATTACK_SPEED(AttributeFormats.ATTACK_SPEED, 0),
    MAX_HUNGER(AttributeFormats.HUNGER, 100),
    SPEED(AttributeFormats.SPEED, 1),
    ;

    private final AttributeFormat format;
    private final double defaultValue;

    PlayerAttribute(AttributeFormat format, double defaultValue) {
        this.format = format;
        this.defaultValue = defaultValue;
    }

    public AttributeFormat getFormat() {
        return format;
    }

    public double getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String toString() {
        return format.displayName();
    }
}
