package com.datasiqn.arcadia.events;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaKeys;
import com.datasiqn.arcadia.loottables.ArcadiaLootTable;
import com.datasiqn.arcadia.loottables.LootTables;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class LootTableEvents implements Listener {
    private final Arcadia plugin;

    public LootTableEvents(Arcadia plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerOpenChest(@NotNull PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if (block == null) return;
        if (block.getType() != Material.CHEST) return;

        Chest chest = (Chest) block.getState();
        PersistentDataContainer pdc = chest.getPersistentDataContainer();
        String lootTableString = pdc.get(ArcadiaKeys.LOOT_TABLE, PersistentDataType.STRING);
        if (lootTableString == null) return;
        ArcadiaLootTable lootTable;
        try {
            lootTable = LootTables.valueOf(lootTableString).getLootTable();
        } catch (IllegalArgumentException e) {
            return;
        }
        pdc.remove(ArcadiaKeys.LOOT_TABLE);
        chest.update();
        lootTable.fillInventory(chest.getInventory(), new Random(), plugin);
    }
}
