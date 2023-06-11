package com.datasiqn.arcadia.enchants;

import com.datasiqn.arcadia.enchants.modifiers.DamageEnchantModifier;
import com.datasiqn.arcadia.item.ArcadiaItem;
import com.datasiqn.arcadia.item.type.ItemType;
import org.jetbrains.annotations.NotNull;

public class EnchantmentSharpness extends Enchantment {
    public EnchantmentSharpness() {
        super(new DamageEnchantModifier(DamageModifierType.ADDITIVE_MULTIPLIER, level -> level * 0.1));
    }

    @Override
    public boolean canEnchant(@NotNull ArcadiaItem item) {
        return item.getItemData().getItemType() == ItemType.SWORD;
    }

    @Override
    public @NotNull String getName() {
        return "Sharpness";
    }
}
