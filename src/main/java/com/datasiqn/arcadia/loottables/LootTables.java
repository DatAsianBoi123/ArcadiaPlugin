package com.datasiqn.arcadia.loottables;

import com.datasiqn.arcadia.items.ArcadiaItem;
import com.datasiqn.arcadia.items.materials.ArcadiaMaterial;
import org.apache.commons.lang.math.IntRange;
import org.bukkit.Material;

public enum LootTables {
    ENTITY_ZOMBIE(new WeightedLootTable.Builder("ENTITY_ZOMBIE")
            .addItem(new WeightedLootTable.LootTableItem(() -> new ArcadiaItem(Material.ROTTEN_FLESH), 1, new IntRange(2, 3)))
            .build()),
    ENTITY_UNDEAD_GUARDIAN(new WeightedLootTable.Builder("ENTITY_UNDEAD_GUARDIAN")
            .addItem(new WeightedLootTable.LootTableItem(() -> new ArcadiaItem(Material.BONE), 1, new IntRange(5, 10)))
            .addItem(new WeightedLootTable.LootTableItem(() -> new ArcadiaItem(ArcadiaMaterial.GUARDIAN_KEY), 0.5, 1))
            .build()),
    ENTITY_IRON_GIANT(new WeightedLootTable.Builder("ENTITY_IRON_GIANT")
            .addItem(new WeightedLootTable.LootTableItem(() -> new ArcadiaItem(Material.IRON_INGOT), 1, new IntRange(32, 64)))
            .addItem(()-> new ArcadiaItem(ArcadiaMaterial.ANCIENT_CORE), 0.01)
            .build()),

    CHEST_DEFAULT(new WeightedLootTable.Builder("CHEST_DEFAULT")
            .addItem(new WeightedLootTable.LootTableItem(() -> new ArcadiaItem(ArcadiaMaterial.ENCHANTED_STICK), 1, 12))
            .addItem(new WeightedLootTable.LootTableItem(() -> new ArcadiaItem(Material.ROTTEN_FLESH), 1, new IntRange(37, 78)))
            .build()),

    EMPTY(new WeightedLootTable.Builder("EMPTY").build()),
    ;

    private final ArcadiaLootTable lootTable;

    LootTables(ArcadiaLootTable lootTable) {
        this.lootTable = lootTable;
    }

    public ArcadiaLootTable getLootTable() {
        return lootTable;
    }
}
