package com.datasiqn.arcadia.loottable;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.item.ArcadiaItem;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.stream.IntStream;

public interface ArcadiaLootTable {
    @NotNull
    @UnmodifiableView
    Collection<@NotNull ArcadiaItem> generateItems(Random random);

    @Contract(mutates = "param1")
    default void fillInventory(Inventory inventory, Random random, Arcadia plugin) {
        Collection<@NotNull ArcadiaItem> items = new HashSet<>(generateItems(random));
        int totalAmount = 0;
        for (ArcadiaItem item : items) totalAmount += item.getAmount();

        int emptySlots = random.nextInt(2, 6);
        int size = inventory.getSize();

        List<ArcadiaItem> itemsSplit = new ArrayList<>();
        while (!items.isEmpty()) {
            ArcadiaItem itemToSplit = items.stream().findAny().get();

            float splitAmount = (itemToSplit.getAmount() / (float) totalAmount) * (size - emptySlots);
            Set<ArcadiaItem> itemSet = getSplitItems(splitAmount, itemToSplit);
            itemsSplit.addAll(itemSet);

            items.remove(itemToSplit);
        }

        itemsSplit.forEach(item -> {
            int[] availableSlots = IntStream.range(0, size).filter(i -> {
                ItemStack itemInSlot = inventory.getItem(i);
                return itemInSlot == null || itemInSlot.getType() == Material.AIR;
            }).toArray();

            if (availableSlots.length == 0) {
                plugin.getLogger().warning("Tried to overfill a container!");
                return;
            }
            inventory.setItem(availableSlots[random.nextInt(availableSlots.length)], item.build());
        });
    }

    @NotNull
    private static Set<ArcadiaItem> getSplitItems(float splitAmount, @NotNull ArcadiaItem itemToSplit) {
        int times = Math.round(splitAmount);
        Set<ArcadiaItem> itemSet = new HashSet<>(times);
        int amount = Math.floorDiv(itemToSplit.getAmount(), times);
        int extraAmounts = itemToSplit.getAmount() % times;
        for (int i = 0; i < times; i++) {
            ArcadiaItem item = new ArcadiaItem(itemToSplit);
            if (extraAmounts > 0) {
                item.setAmount(amount + 1);
                extraAmounts -= 1;
            } else {
                item.setAmount(amount);
            }
            if (item.getAmount() > 0) itemSet.add(item);
        }
        return itemSet;
    }
}
