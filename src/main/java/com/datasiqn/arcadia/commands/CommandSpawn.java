package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.ArcadiaKeys;
import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.loottables.LootTables;
import com.datasiqn.commandcore.commands.Command;
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

public class CommandSpawn {
    public Command getCommand() {
        return new CommandBuilder<>(Player.class)
                .permission(ArcadiaPermission.PERMISSION_USE_SPAWN)
                .then(LiteralBuilder.<Player>literal("upgradechest")
                        .executes(context -> {
                            Player player = context.getSender();
                            Location location = player.getLocation();
                            World world = player.getWorld();
                            world.setType(location, Material.ENDER_CHEST);
                            EnderChest enderChest = (EnderChest) world.getBlockAt(location).getState();
                            enderChest.getPersistentDataContainer().set(ArcadiaKeys.UPGRADE_CHEST, PersistentDataType.BYTE, (byte) 1);
                            enderChest.update();
                        }))
                .then(LiteralBuilder.<Player>literal("lootchest")
                        .executes(context -> {
                            Player player = context.getSender();
                            Location location = player.getLocation();
                            World world = player.getWorld();
                            world.setType(location, Material.CHEST);
                            Chest chest = (Chest) world.getBlockAt(location).getState();
                            PersistentDataContainer pdc = chest.getPersistentDataContainer();
                            pdc.set(ArcadiaKeys.LOOT_TABLE, PersistentDataType.STRING, LootTables.CHEST_DEFAULT.name());
                            chest.update();
                        }))
                .build();
    }
}
