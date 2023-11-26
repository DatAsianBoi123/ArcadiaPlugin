package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.ArcadiaTag;
import com.datasiqn.arcadia.commands.argument.ArcadiaArgumentType;
import com.datasiqn.arcadia.loottable.LootTable;
import com.datasiqn.arcadia.util.PdcUtil;
import com.datasiqn.commandcore.command.builder.ArgumentBuilder;
import com.datasiqn.commandcore.command.builder.CommandBuilder;
import com.datasiqn.commandcore.command.builder.LiteralBuilder;
import com.datasiqn.commandcore.locatable.LocatableCommandSender;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.block.EnderChest;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

public class CommandSpawn {
    public CommandBuilder getCommand() {
        return new CommandBuilder("spawn")
                .permission(ArcadiaPermission.PERMISSION_USE_SPAWN)
                .description("Spawns different types of chests")
                .then(LiteralBuilder.literal("upgradechest")
                        .requiresLocatable()
                        .executes((context, source, arguments) -> {
                            LocatableCommandSender locatable = source.getLocatable();
                            Location location = locatable.getLocation();
                            World world = locatable.getWorld();
                            world.setType(location, Material.ENDER_CHEST);
                            EnderChest enderChest = (EnderChest) world.getBlockAt(location).getState();
                            PersistentDataContainer pdc = enderChest.getPersistentDataContainer();
                            PdcUtil.set(pdc, ArcadiaTag.UPGRADE_CHEST, true);
                            enderChest.update();
                        }))
                .then(LiteralBuilder.literal("lootchest")
                        .then(ArgumentBuilder.argument(ArcadiaArgumentType.LOOT_TABLE, "loot table")
                                .requiresLocatable()
                                .executes((context, source, arguments) -> {
                                    LootTable lootTable = arguments.get(1, ArcadiaArgumentType.LOOT_TABLE);
                                    spawnLootChest(source.getLocatable().getLocation(), lootTable);
                                }))
                        .requiresPlayer()
                        .executes((context, source, arguments) -> spawnLootChest(source.getPlayer().getLocation(), LootTable.CHEST_DEFAULT)));
    }

    private void spawnLootChest(@NotNull Location location, LootTable lootTable) {
        World world = location.getWorld();
        if (world == null) return;
        world.setType(location, Material.CHEST);
        Chest chest = (Chest) world.getBlockAt(location).getState();
        PersistentDataContainer pdc = chest.getPersistentDataContainer();
        PdcUtil.set(pdc, ArcadiaTag.LOOT_TABLE, lootTable);
        chest.update();
    }
}
