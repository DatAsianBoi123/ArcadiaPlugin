package com.datasiqn.arcadia.enchants;

import com.datasiqn.arcadia.enchants.modifiers.EntityEnchantModifier;
import com.datasiqn.arcadia.items.ArcadiaItem;
import com.datasiqn.arcadia.items.type.ItemType;
import org.jetbrains.annotations.NotNull;

public class EnchantFire extends Enchantment {
    public EnchantFire() {
        super(new EntityEnchantModifier(((entity, level) -> entity.setSecondsOnFire(10))));
    }

    @Override
    public boolean canEnchant(@NotNull ArcadiaItem item) {
        return item.getItemData().getItemType() == ItemType.SWORD;
    }

    @Override
    public @NotNull String getName() {
        return "Fire Aspect";
    }
}
