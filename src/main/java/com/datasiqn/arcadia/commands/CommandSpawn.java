package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.ArcadiaTag;
import com.datasiqn.arcadia.commands.argument.ArcadiaArgumentType;
import com.datasiqn.arcadia.loottable.LootTables;
import com.datasiqn.arcadia.util.PdcUtil;
import com.datasiqn.commandcore.command.builder.ArgumentBuilder;
import com.datasiqn.commandcore.command.builder.CommandBuilder;
import com.datasiqn.commandcore.command.builder.LiteralBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.block.EnderChest;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

public class CommandSpawn {
    public CommandBuilder getCommand() {
        return new CommandBuilder("spawn")
                .permission(ArcadiaPermission.PERMISSION_USE_SPAWN)
                .then(LiteralBuilder.literal("upgradechest")
                        .requiresPlayer()
                        .executes(context -> {
                            Player player = context.getSource().getPlayer().unwrap();
                            Location location = player.getLocation();
                            World world = player.getWorld();
                            world.setType(location, Material.ENDER_CHEST);
                            EnderChest enderChest = (EnderChest) world.getBlockAt(location).getState();
                            PersistentDataContainer pdc = enderChest.getPersistentDataContainer();
                            PdcUtil.set(pdc, ArcadiaTag.UPGRADE_CHEST, true);
                            enderChest.update();
                        }))
                .then(LiteralBuilder.literal("lootchest")
                        .then(ArgumentBuilder.argument(ArcadiaArgumentType.LOOT_TABLE, "loot table")
                                .requiresPlayer()
                                .executes(context -> {
                                    LootTables lootTable = context.getArguments().get(1, ArcadiaArgumentType.LOOT_TABLE).unwrap();
                                    spawnLootChest(context.getSource().getPlayer().unwrap().getLocation(), lootTable);
                                }))
                        .requiresPlayer()
                        .executes(context -> spawnLootChest(context.getSource().getPlayer().unwrap().getLocation(), LootTables.CHEST_DEFAULT)));
    }

    private void spawnLootChest(@NotNull Location location, LootTables lootTable) {
        World world = location.getWorld();
        if (world == null) return;
        world.setType(location, Material.CHEST);
        Chest chest = (Chest) world.getBlockAt(location).getState();
        PersistentDataContainer pdc = chest.getPersistentDataContainer();
        PdcUtil.set(pdc, ArcadiaTag.LOOT_TABLE, lootTable);
        chest.update();
    }
}
