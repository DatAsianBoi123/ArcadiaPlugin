package com.datasiqn.arcadia.entities.loottables;

import com.datasiqn.arcadia.items.ArcadiaItem;
import com.datasiqn.arcadia.items.materials.ArcadiaMaterial;
import org.apache.commons.lang.math.IntRange;
import org.bukkit.Material;

public enum LootTables {
    ZOMBIE(new ArcadiaLootTable.Builder().addItem(new ArcadiaLootTable.LootTableItem(() -> new ArcadiaItem(Material.ROTTEN_FLESH), 1, new IntRange(2, 3)))),
    UNDEAD_GUARDIAN(new ArcadiaLootTable.Builder()
            .addItem(new ArcadiaLootTable.LootTableItem(() -> new ArcadiaItem(Material.BONE), 1, new IntRange(5, 10)))
            .addItem(new ArcadiaLootTable.LootTableItem(() -> new ArcadiaItem(ArcadiaMaterial.GUARDIAN_KEY), 0.5, 1))),
    IRON_GIANT(new ArcadiaLootTable.Builder()
            .addItem(new ArcadiaLootTable.LootTableItem(() -> new ArcadiaItem(Material.IRON_INGOT), 1, new IntRange(32, 64)))
            .addItem(()-> new ArcadiaItem(ArcadiaMaterial.ANCIENT_CORE), 0.01)),
    EMPTY(new ArcadiaLootTable.Builder());

    private final ArcadiaLootTable lootTable;

    LootTables(ArcadiaLootTable lootTable) {
        this.lootTable = lootTable;
    }

    public ArcadiaLootTable getLootTable() {
        return lootTable;
    }
}
