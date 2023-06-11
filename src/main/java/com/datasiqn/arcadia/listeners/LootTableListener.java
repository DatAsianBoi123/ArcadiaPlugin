package com.datasiqn.arcadia.listeners;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaTag;
import com.datasiqn.arcadia.loottables.LootTables;
import com.datasiqn.arcadia.util.PdcUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class LootTableListener implements Listener {
    private final Arcadia plugin;

    public LootTableListener(Arcadia plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerOpenChest(@NotNull PlayerInteractEvent event) {
        if (event.useInteractedBlock() == Event.Result.DENY) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if (block == null) return;
        if (block.getType() != Material.CHEST) return;

        Chest chest = (Chest) block.getState();
        PersistentDataContainer pdc = chest.getPersistentDataContainer();
        LootTables lootTable = PdcUtil.get(pdc, ArcadiaTag.LOOT_TABLE);
        if (lootTable == null) return;
        PdcUtil.remove(pdc, ArcadiaTag.LOOT_TABLE);
        chest.update();
        lootTable.getLootTable().fillInventory(chest.getInventory(), new Random(), plugin);
    }
}
