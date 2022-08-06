package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.commands.arguments.ArgumentType;
import com.datasiqn.arcadia.commands.builder.ArgumentBuilder;
import com.datasiqn.arcadia.commands.builder.CommandBuilder;
import com.datasiqn.arcadia.items.ArcadiaItem;
import com.datasiqn.arcadia.items.types.ArcadiaMaterial;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class CommandItem {
    public ArcadiaCommand getCommand() {
        return new CommandBuilder<>(Player.class)
                .permission(ArcadiaPermission.PERMISSION_USE_ITEM)
                .description("Gives you a custom Arcadia Item")
                .then(ArgumentBuilder.<Player, ArcadiaMaterial>argument(ArgumentType.ITEM, "item")
                        .then(ArgumentBuilder.<Player, Integer>argument(ArgumentType.INTEGER, "amount")
                                .executes(context -> {
                                    ArcadiaMaterial material = context.parseArgument(ArgumentType.ITEM, 0);
                                    int amount = context.parseArgument(ArgumentType.INTEGER, 1);
                                    PlayerInventory inventory = context.sender().get().getInventory();
                                    if (material.getItemData().isStackable()) {
                                        inventory.addItem(new ArcadiaItem(material).build(amount));
                                    } else {
                                        for (int i = 0; i < amount; i++) {
                                            inventory.addItem(new ArcadiaItem(material).build());
                                        }
                                    }
                                }))
                        .executes(context -> context.sender().get().getInventory().addItem(new ArcadiaItem(context.parseArgument(ArgumentType.ITEM, 0)).build())))
                .build();
    }
}
