package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.commands.arguments.ArcadiaArgumentType;
import com.datasiqn.arcadia.items.ArcadiaItem;
import com.datasiqn.arcadia.items.materials.ArcadiaMaterial;
import com.datasiqn.commandcore.arguments.ArgumentType;
import com.datasiqn.commandcore.commands.Command;
import com.datasiqn.commandcore.commands.builder.ArgumentBuilder;
import com.datasiqn.commandcore.commands.builder.CommandBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class CommandItem {
    public Command getCommand() {
        return new CommandBuilder<>(Player.class)
                .permission(ArcadiaPermission.PERMISSION_USE_ITEM)
                .description("Gives you a custom Arcadia Item")
                .then(ArgumentBuilder.<Player, ArcadiaMaterial>argument(ArcadiaArgumentType.ITEM, "item")
                        .then(ArgumentBuilder.<Player, Integer>argument(ArgumentType.NATURAL_NUMBER, "amount")
                                .executes(context -> {
                                    ArcadiaMaterial material = context.parseArgument(ArcadiaArgumentType.ITEM, 0);
                                    int amount = context.parseArgument(ArgumentType.NATURAL_NUMBER, 1);
                                    PlayerInventory inventory = context.getSender().getInventory();
                                    ArcadiaItem item = new ArcadiaItem(material);
                                    if (item.getItemData().isStackable()) {
                                        item.setAmount(amount);
                                        inventory.addItem(item.build());
                                    } else {
                                        for (int i = 0; i < amount; i++) {
                                            inventory.addItem(item.build());
                                        }
                                    }
                                }))
                        .executes(context -> context.getSender().getInventory().addItem(new ArcadiaItem(context.parseArgument(ArcadiaArgumentType.ITEM, 0)).build())))
                .build();
    }
}
