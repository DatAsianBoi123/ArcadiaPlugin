package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.commands.argument.ArcadiaArgumentType;
import com.datasiqn.arcadia.item.ArcadiaItem;
import com.datasiqn.commandcore.command.builder.ArgumentBuilder;
import com.datasiqn.commandcore.command.builder.CommandBuilder;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class CommandLoot {
    private static final Random random = new Random();

    public CommandBuilder getCommand() {
        return new CommandBuilder("loot")
                .permission(ArcadiaPermission.PERMISSION_USE_LOOT)
                .description("Loots a specific loot table")
                .then(ArgumentBuilder.argument(ArcadiaArgumentType.LOOT_TABLE, "loot table")
                        .requiresPlayer()
                        .executes((context, source, arguments) -> source.getPlayer().getInventory().addItem(arguments.get(0, ArcadiaArgumentType.LOOT_TABLE).getLootTable().generateItems(random).stream().map(ArcadiaItem::build).toArray(ItemStack[]::new))));
    }
}
