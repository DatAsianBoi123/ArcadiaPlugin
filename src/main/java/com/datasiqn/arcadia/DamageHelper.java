package com.datasiqn.arcadia;

public abstract class DamageHelper {
    public static double getStrengthMultiplier(double strength) {
        return (10 + strength) / 10;
    }

    public static double getFinalDamageWithDefense(double finalDamage, double defense) {
        return finalDamage * (1 - getDamageReduction(defense));
    }

    public static double getDamageReduction(double defense) {
        return defense / (defense + 20);
    }
}
