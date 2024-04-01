package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.item.ArcadiaItem;
import com.datasiqn.arcadia.loottable.LootTable;
import com.datasiqn.commandcore.command.annotation.AnnotationCommand;
import com.datasiqn.commandcore.command.annotation.Argument;
import com.datasiqn.commandcore.command.annotation.CommandDescription;
import com.datasiqn.commandcore.command.annotation.Executor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

@CommandDescription(name = "loot", description = "Loots an Arcadia loot table", permission = ArcadiaPermission.PERMISSION_USE_LOOT)
public class CommandLoot implements AnnotationCommand {
    private static final Random random = new Random();

    @Executor
    public void loot(Player player,
                     @Argument(name = "loot table") LootTable lootTable) {
        player.getInventory().addItem(lootTable.getLootTable().generateItems(random).stream().map(ArcadiaItem::build).toArray(ItemStack[]::new));
    }
}
