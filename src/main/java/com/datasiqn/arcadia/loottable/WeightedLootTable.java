package com.datasiqn.arcadia.loottable;

import com.datasiqn.arcadia.item.ArcadiaItem;
import org.apache.commons.lang.math.IntRange;
import org.jetbrains.annotations.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.function.Supplier;

public class WeightedLootTable implements ArcadiaLootTable {
    @Unmodifiable
    private final Collection<LootTableItem> items;

    @Contract(pure = true)
    public WeightedLootTable(@NotNull Builder builder) {
        this.items = Collections.unmodifiableCollection(builder.items);
    }

    @Override
    public @NotNull @UnmodifiableView Collection<@NotNull ArcadiaItem> generateItems(Random random) {
        Collection<@NotNull ArcadiaItem> arcadiaItems = new HashSet<>();
        items.forEach(item -> {
            ArcadiaItem arcadiaItem = item.generateItem(random);
            if (arcadiaItem != null) arcadiaItems.add(arcadiaItem);
        });
        return Collections.unmodifiableCollection(arcadiaItems);
    }

    public static class Builder {
        private final Collection<LootTableItem> items = new HashSet<>();

        @Contract(mutates = "this")
        public Builder addItem(Supplier<ArcadiaItem> itemSupplier, double dropChance) {
            return addItem(new LootTableItem(itemSupplier, dropChance, 1));
        }
        @Contract(mutates = "this")
        public Builder addItem(LootTableItem itemStack) {
            items.add(itemStack);
            return this;
        }

        public WeightedLootTable build() {
            return new WeightedLootTable(this);
        }
    }

    public static class LootTableItem {
        private final Supplier<ArcadiaItem> itemSupplier;
        private final double dropChance;
        private final IntRange amounts;

        public LootTableItem(Supplier<ArcadiaItem> itemSupplier, @Range(from = 0, to = 1) double dropChance, int amount) {
            this(itemSupplier, dropChance, new IntRange(amount));
        }

        public LootTableItem(Supplier<ArcadiaItem> itemSupplier, @Range(from = 0, to = 1) double dropChance, IntRange amounts) {
            this.itemSupplier = itemSupplier;
            this.dropChance = dropChance;
            this.amounts = amounts;
        }

        @Contract(pure = true)
        @Nullable
        public ArcadiaItem generateItem(@NotNull Random random) {
            if (random.nextDouble() < dropChance) {
                ArcadiaItem item = itemSupplier.get();
                item.setAmount(random.nextInt(amounts.getMinimumInteger(), amounts.getMaximumInteger() + 1));
                return item;
            }

            return null;
        }
    }
}
