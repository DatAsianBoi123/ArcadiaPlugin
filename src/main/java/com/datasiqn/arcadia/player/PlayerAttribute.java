package com.datasiqn.arcadia.player;

import com.datasiqn.arcadia.item.stat.ItemAttribute;

public enum PlayerAttribute {
    MAX_HEALTH(ItemAttribute.HEALTH, 10),
    DEFENSE(ItemAttribute.DEFENSE, 0),
    STRENGTH(ItemAttribute.STRENGTH, 0),
    ATTACK_SPEED(ItemAttribute.ATTACK_SPEED, 0),
    MAX_HUNGER(ItemAttribute.HUNGER, 100),
    SPEED(ItemAttribute.SPEED, 1),
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
