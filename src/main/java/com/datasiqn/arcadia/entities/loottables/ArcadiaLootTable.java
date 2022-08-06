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

public interface ArcadiaLootTable {

    void spawnItems(Random random, Consumer<ItemStack> itemConsumer);

    class Builder implements ArcadiaLootTable {
        private final Collection<LootTableItem> items = new HashSet<>();

        @Contract("_, _ -> this")
        public Builder addItem(ArcadiaItem item, double dropChance) {
            items.add(new LootTableItem(item, dropChance, 1));
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
        private final ArcadiaItem itemStack;
        private final double dropChance;
        private final IntRange amounts;

        public LootTableItem(ArcadiaItem itemStack, @Range(from = 0, to = 1) double dropChance, int amount) {
            this(itemStack, dropChance, new IntRange(amount));
        }
        public LootTableItem(ArcadiaItem itemStack, @Range(from = 0, to = 1) double dropChance, IntRange amounts) {
            this.itemStack = itemStack;
            this.dropChance = dropChance;
            this.amounts = amounts;
        }

        @Contract(pure = true)
        @Nullable
        public ItemStack generateItem(@NotNull Random random) {
            if (random.nextDouble() < dropChance) {
                return itemStack.build(random.nextInt(amounts.getMinimumInteger(), amounts.getMaximumInteger() + 1));
            }

            return null;
        }
    }
}
