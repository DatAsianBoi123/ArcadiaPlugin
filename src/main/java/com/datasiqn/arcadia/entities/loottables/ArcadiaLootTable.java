package com.datasiqn.arcadia.entities.loottables;

import com.datasiqn.arcadia.items.ArcadiaItem;
import org.apache.commons.lang.math.IntRange;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ArcadiaLootTable {
    void spawnItems(Random random, Consumer<ItemStack> itemConsumer);

    class Builder implements ArcadiaLootTable {
        private final Collection<LootTableItem> items = new HashSet<>();

        @Contract("_, _ -> this")
        public Builder addItem(Supplier<ArcadiaItem> itemSupplier, double dropChance) {
            items.add(new LootTableItem(itemSupplier, dropChance, 1));
            return this;
        }
        @Contract("_ -> this")
        public Builder addItem(LootTableItem itemStack) {
            items.add(itemStack);
            return this;
        }

        @Override
        public void spawnItems(Random random, Consumer<ItemStack> itemConsumer) {
            items.stream().map(lootTableItem -> lootTableItem.generateItem(random)).forEach(itemStack -> {
                if (itemStack != null) itemConsumer.accept(itemStack);
            });
        }
    }

    class LootTableItem {
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
        public ItemStack generateItem(@NotNull Random random) {
            if (random.nextDouble() < dropChance) {
                ArcadiaItem item = itemSupplier.get();
                item.setAmount(random.nextInt(amounts.getMinimumInteger(), amounts.getMaximumInteger() + 1));
                return item.build();
            }

            return null;
        }
    }
}
