package com.datasiqn.arcadia.item.modifiers;

import org.bukkit.Color;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PotionModifier implements ItemModifier {
    private final Color color;

    public PotionModifier(Color color) {
        this.color = color;
    }

    @Override
    public void modify(@NotNull UUID uuid, @Nullable ItemMeta metaCopy) {
        if (!(metaCopy instanceof PotionMeta meta)) return;

        meta.setColor(color);
    }
}
