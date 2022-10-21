package com.datasiqn.arcadia.items.modifiers;

import org.bukkit.Color;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class LeatherArmorItemModifier implements ItemModifier {
    private final Color color;

    public LeatherArmorItemModifier(Color color) {
        this.color = color;
    }

    @Override
    public void modify(@NotNull UUID uuid, @Nullable ItemMeta metaCopy) {
        if (!(metaCopy instanceof LeatherArmorMeta armorMeta)) return;

        armorMeta.setColor(color);
    }
}
