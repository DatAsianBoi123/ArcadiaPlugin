package com.datasiqn.arcadia;

public final class DamageHelper {
    private DamageHelper() {}

    public static double getStrengthBonus(double damage, double strength) {
        return 5 * (damage / 100) * Math.pow(strength, 2);
    }

    public static double getFinalDamageWithDefense(double finalDamage, double defense) {
        return finalDamage * (1 - getDamageReduction(defense));
    }

    public static double getDamageReduction(double defense) {
        return defense / (defense + 20);
    }
}
