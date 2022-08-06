package com.datasiqn.arcadia.items.modifiers;

import com.datasiqn.arcadia.items.data.ItemData;
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
    public @Nullable ItemMeta modify(@NotNull ItemData itemData, @NotNull UUID uuid, @Nullable ItemMeta metaCopy) {
        if (!(metaCopy instanceof LeatherArmorMeta armorMeta)) return null;

        armorMeta.setColor(color);
        return armorMeta;
    }
}
