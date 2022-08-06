package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.commands.arguments.ArgumentType;
import com.datasiqn.arcadia.commands.builder.ArgumentBuilder;
import com.datasiqn.arcadia.commands.builder.CommandBuilder;
import com.datasiqn.arcadia.entities.loottables.LootTables;
import org.bukkit.entity.Player;

import java.util.Random;

public class CommandLoot {
    private static final Random random = new Random();

    public ArcadiaCommand getCommand() {
        return new CommandBuilder<>(Player.class)
                .permission(ArcadiaPermission.PERMISSION_USE_LOOT)
                .description("Loots a specific loot table")
                .then(ArgumentBuilder.<Player, LootTables>argument(ArgumentType.LOOT_TABLE, "loot table")
                        .executes(context -> context.parseArgument(ArgumentType.LOOT_TABLE, 0).getLootTable().spawnItems(random, context.sender().get().getInventory()::addItem)))
                .build();
    }
}
