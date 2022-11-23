package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.ArcadiaKeys;
import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.commands.arguments.ArcadiaArgumentType;
import com.datasiqn.arcadia.datatype.ArcadiaDataType;
import com.datasiqn.arcadia.loottables.ArcadiaLootTable;
import com.datasiqn.arcadia.loottables.LootTables;
import com.datasiqn.commandcore.commands.builder.ArgumentBuilder;
import com.datasiqn.commandcore.commands.builder.CommandBuilder;
import com.datasiqn.commandcore.commands.builder.LiteralBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.block.EnderChest;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class CommandSpawn {
    public CommandBuilder getCommand() {
        return new CommandBuilder()
                .permission(ArcadiaPermission.PERMISSION_USE_SPAWN)
                .then(LiteralBuilder.literal("upgradechest")
                        .requiresPlayer()
                        .executes(context -> {
                            Player player = context.getSource().getPlayer().unwrap();
                            Location location = player.getLocation();
                            World world = player.getWorld();
                            world.setType(location, Material.ENDER_CHEST);
                            EnderChest enderChest = (EnderChest) world.getBlockAt(location).getState();
                            enderChest.getPersistentDataContainer().set(ArcadiaKeys.UPGRADE_CHEST, ArcadiaDataType.BOOLEAN, true);
                            enderChest.update();
                        }))
                .then(LiteralBuilder.literal("lootchest")
                        .then(ArgumentBuilder.argument(ArcadiaArgumentType.LOOT_TABLE, "loot table")
                                .requiresPlayer()
                                .executes(context -> context.getArguments().get(1, ArcadiaArgumentType.LOOT_TABLE).ifOk(lootTable -> spawnLootChest(context.getSource().getPlayer().unwrap().getLocation(), lootTable.getLootTable()))))
                        .requiresPlayer()
                        .executes(context -> spawnLootChest(context.getSource().getPlayer().unwrap().getLocation(), LootTables.CHEST_DEFAULT.getLootTable())));
    }

    private void spawnLootChest(@NotNull Location location, ArcadiaLootTable lootTable) {
        World world = location.getWorld();
        if (world == null) return;
        world.setType(location, Material.CHEST);
        Chest chest = (Chest) world.getBlockAt(location).getState();
        PersistentDataContainer pdc = chest.getPersistentDataContainer();
        pdc.set(ArcadiaKeys.LOOT_TABLE, PersistentDataType.STRING, lootTable.getId());
        chest.update();
    }
}
