package com.datasiqn.arcadia.players;

import com.datasiqn.arcadia.items.stats.ItemAttribute;

public enum PlayerAttribute {
    MAX_HEALTH(ItemAttribute.HEALTH, 10),
    DEFENSE(ItemAttribute.DEFENSE, 0),
    STRENGTH(ItemAttribute.STRENGTH, 0),
    ATTACK_SPEED(ItemAttribute.ATTACK_SPEED, 0),
    MAX_HUNGER(ItemAttribute.HUNGER, 100),
    ;

    private final ItemAttribute itemAttribute;
    private final double defaultValue;

    PlayerAttribute(ItemAttribute itemAttribute, double defaultValue) {
        this.itemAttribute = itemAttribute;
        this.defaultValue = defaultValue;
    }

    public ItemAttribute getItemAttribute() {
        return itemAttribute;
    }

    public double getDefaultValue() {
        return defaultValue;
    }
}
