package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.ArcadiaTag;
import com.datasiqn.arcadia.loottable.LootTable;
import com.datasiqn.arcadia.util.PdcUtil;
import com.datasiqn.commandcore.command.annotation.AnnotationCommand;
import com.datasiqn.commandcore.command.annotation.Argument;
import com.datasiqn.commandcore.command.annotation.CommandDescription;
import com.datasiqn.commandcore.command.annotation.LiteralExecutor;
import com.datasiqn.commandcore.locatable.LocatableCommandSender;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.block.EnderChest;
import org.bukkit.persistence.PersistentDataContainer;

@CommandDescription(name = "spawn", description = "Spawns different types of chests", permission = ArcadiaPermission.PERMISSION_USE_SPAWN)
public class CommandSpawn implements AnnotationCommand {
    @LiteralExecutor("upgradechest")
    public void upgradeChest(LocatableCommandSender locatable) {
        Location location = locatable.getLocation();
        World world = locatable.getWorld();
        world.setType(location, Material.ENDER_CHEST);
        EnderChest enderChest = (EnderChest) world.getBlockAt(location).getState();
        PersistentDataContainer pdc = enderChest.getPersistentDataContainer();
        PdcUtil.set(pdc, ArcadiaTag.UPGRADE_CHEST, true);
        enderChest.update();
    }

    @LiteralExecutor("lootchest")
    public void lootChest(LocatableCommandSender locatable,
                          @Argument(name = "loot table") LootTable lootTable) {
        Location location = locatable.getLocation();
        World world = locatable.getWorld();
        world.setType(location, Material.CHEST);
        Chest chest = (Chest) world.getBlockAt(location).getState();
        PersistentDataContainer pdc = chest.getPersistentDataContainer();
        PdcUtil.set(pdc, ArcadiaTag.LOOT_TABLE, lootTable);
        chest.update();
    }
}
