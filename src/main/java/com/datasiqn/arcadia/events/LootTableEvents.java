package com.datasiqn.arcadia.events;

import com.datasiqn.arcadia.Arcadia;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
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
        System.out.println("yes");

        Chest chest = (Chest) block.getState();
        LootTable lootTable = chest.getLootTable();
        if (lootTable == null) return;
        System.out.println(lootTable.getKey().getNamespace());
        if (lootTable.getKey().getNamespace().equals(plugin.getName())) {
            LootContext lootContext = new LootContext.Builder(block.getLocation())
                    .build();
            lootTable.fillInventory(chest.getBlockInventory(), new Random(), lootContext);
            chest.update();
        }
    }
}
