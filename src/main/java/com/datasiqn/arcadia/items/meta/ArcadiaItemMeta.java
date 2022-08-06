package com.datasiqn.arcadia.items.meta;

import com.datasiqn.arcadia.enchants.EnchantType;
import com.datasiqn.arcadia.items.stats.ItemStats;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public interface ArcadiaItemMeta {
    @NotNull UUID getUuid();

    @NotNull ItemStats getItemStats();

    double getItemQuality();

    double getItemQualityBonus();

    void setItemQualityBonus(double newAmount);

    boolean hasEnchants();

    boolean hasEnchant(EnchantType type);

    int getEnchantLevel(EnchantType type);

    @Contract(" -> new")
    @NotNull Map<EnchantType, Integer> getEnchants();

    void addEnchant(EnchantType type, int level);

    void removeEnchant(EnchantType type);

    void clearEnchants();
}
