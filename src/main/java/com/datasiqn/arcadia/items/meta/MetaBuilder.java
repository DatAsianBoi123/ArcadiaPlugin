package com.datasiqn.arcadia.items.meta;

import com.datasiqn.arcadia.enchants.EnchantType;
import com.datasiqn.arcadia.items.stats.AttributeInstance;
import com.datasiqn.arcadia.items.stats.AttributeRange;
import com.datasiqn.arcadia.items.stats.ItemAttribute;
import com.datasiqn.arcadia.items.stats.ItemStats;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class MetaBuilder {
    private final Map<ItemAttribute, AttributeRange> attributes = new HashMap<>();

    public MetaBuilder setAttribute(ItemAttribute attribute, AttributeRange range) {
        attributes.put(attribute, range);
        return this;
    }
    public MetaBuilder setAttribute(ItemAttribute attribute, double value) {
        attributes.put(attribute, new AttributeRange(value, value));
        return this;
    }

    public ArcadiaItemMeta build(@NotNull UUID uuid) {
        ItemStats itemStats = new ItemStats();
        attributes.forEach((itemAttribute, range) -> {
            if (!range.hasRange()) {
                itemStats.setAttribute(itemAttribute, new AttributeInstance(range.min()));
                return;
            }
            itemStats.setAttribute(itemAttribute, new AttributeInstance(range));
        });
        return new CustomMeta(uuid, itemStats);
    }

    private static class CustomMeta implements ArcadiaItemMeta {
        private final Map<EnchantType, Integer> enchants = new HashMap<>();
        private final UUID uuid;
        private final ItemStats itemStats;
        private final double itemQuality;

        private double qualityBonus = 0;

        public CustomMeta(@NotNull UUID uuid, @NotNull ItemStats itemStats) {
            this.uuid = uuid;
            this.itemStats = itemStats;
            this.itemQuality = new Random(uuid.getMostSignificantBits()).nextDouble();
            this.itemStats.setItemQuality(itemQuality);
        }

        @Override
        public @NotNull UUID getUuid() {
            return uuid;
        }

        @Override
        public @NotNull ItemStats getItemStats() {
            return itemStats;
        }

        @Override
        public double getItemQuality() {
            return itemQuality;
        }

        @Override
        public double getItemQualityBonus() {
            return qualityBonus;
        }

        @Override
        public void setItemQualityBonus(double newAmount) {
            this.qualityBonus = Math.min(1 - itemQuality, newAmount);
            itemStats.setItemQuality(itemQuality + qualityBonus);
        }

        @Override
        public boolean hasEnchants() {
            return !enchants.isEmpty();
        }

        @Override
        public boolean hasEnchant(EnchantType type) {
            return enchants.containsKey(type);
        }

        @Override
        public int getEnchantLevel(EnchantType type) {
            Integer level = enchants.get(type);
            return level == null ? 0 : level;
        }

        @Override
        @Contract(" -> new")
        public @NotNull Map<EnchantType, Integer> getEnchants() {
            return new HashMap<>(enchants);
        }

        @Override
        public void addEnchant(EnchantType type, int level) {
            enchants.put(type, level);
        }

        @Override
        public void removeEnchant(EnchantType type) {
            enchants.remove(type);
        }

        @Override
        public void clearEnchants() {
            enchants.clear();
        }
    }
}
