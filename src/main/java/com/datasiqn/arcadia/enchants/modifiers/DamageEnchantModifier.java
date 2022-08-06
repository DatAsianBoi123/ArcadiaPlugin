package com.datasiqn.arcadia.enchants.modifiers;

import com.datasiqn.arcadia.enchants.DamageModifierType;

import java.util.function.IntToDoubleFunction;

public class DamageEnchantModifier implements EnchantModifier {
    private final IntToDoubleFunction getMultiplier;
    private final DamageModifierType type;

    public DamageEnchantModifier(DamageModifierType type, IntToDoubleFunction getMultiplier) {
        this.getMultiplier = getMultiplier;
        this.type = type;
    }

    public double getMultiplier(int level) {
        return getMultiplier.applyAsDouble(level);
    }

    public DamageModifierType getType() {
        return type;
    }
}
