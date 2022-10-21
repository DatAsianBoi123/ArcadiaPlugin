package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.commands.arguments.ArcadiaArgumentType;
import com.datasiqn.arcadia.entities.loottables.LootTables;
import com.datasiqn.commandcore.commands.Command;
import com.datasiqn.commandcore.commands.builder.ArgumentBuilder;
import com.datasiqn.commandcore.commands.builder.CommandBuilder;
import org.bukkit.entity.Player;

import java.util.Random;

public class CommandLoot {
    private static final Random random = new Random();

    public Command getCommand() {
        return new CommandBuilder<>(Player.class)
                .permission(ArcadiaPermission.PERMISSION_USE_LOOT)
                .description("Loots a specific loot table")
                .then(ArgumentBuilder.<Player, LootTables>argument(ArcadiaArgumentType.LOOT_TABLE, "loot table")
                        .executes(context -> context.parseArgument(ArcadiaArgumentType.LOOT_TABLE, 0).getLootTable().spawnItems(random, context.getSender().getInventory()::addItem)))
                .build();
    }
}
