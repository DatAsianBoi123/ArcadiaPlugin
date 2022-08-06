package com.datasiqn.arcadia.enchants;

@SuppressWarnings("unused")
public enum EnchantType {
    SHARPNESS(new EnchantmentSharpness()),
    FIRE(new EnchantFire());

    private final Enchantment enchantment;

    EnchantType(Enchantment enchantment) {
        this.enchantment = enchantment;
    }

    public Enchantment getEnchantment() {
        return enchantment;
    }
}
