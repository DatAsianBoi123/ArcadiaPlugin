package com.datasiqn.arcadia.enchants;

import com.datasiqn.arcadia.enchants.modifiers.EnchantModifier;
import com.datasiqn.arcadia.item.ArcadiaItem;
import org.jetbrains.annotations.NotNull;

public abstract class Enchantment {
    private final EnchantModifier modifier;

    public Enchantment(EnchantModifier modifier) {
        this.modifier = modifier;
    }

    public abstract boolean canEnchant(@NotNull ArcadiaItem item);

    @NotNull
    public abstract String getName();

    public final EnchantModifier getModifier() {
        return modifier;
    }
}
