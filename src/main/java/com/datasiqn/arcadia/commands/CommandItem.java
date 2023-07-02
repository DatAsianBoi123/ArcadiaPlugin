package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.commands.argument.ArcadiaArgumentType;
import com.datasiqn.arcadia.item.ArcadiaItem;
import com.datasiqn.arcadia.item.material.ArcadiaMaterial;
import com.datasiqn.commandcore.argument.type.ArgumentType;
import com.datasiqn.commandcore.command.builder.ArgumentBuilder;
import com.datasiqn.commandcore.command.builder.CommandBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

public class CommandItem {
    public CommandBuilder getCommand() {
        return new CommandBuilder("i")
                .permission(ArcadiaPermission.PERMISSION_USE_ITEM)
                .description("Gives you a custom Arcadia Item")
                .then(ArgumentBuilder.argument(ArcadiaArgumentType.ITEM, "item")
                        .then(ArgumentBuilder.argument(ArgumentType.NATURAL_NUMBER, "amount")
                                .requiresPlayer()
                                .executes(context -> giveItem(context.getSource().getPlayer().unwrap(), context.getArguments().get(0, ArcadiaArgumentType.ITEM).unwrap(), context.getArguments().get(1, ArgumentType.NATURAL_NUMBER).unwrap())))
                        .requiresPlayer()
                        .executes(context -> giveItem(context.getSource().getPlayer().unwrap(), context.getArguments().get(0, ArcadiaArgumentType.ITEM).unwrap(), 1)));
    }

    private void giveItem(@NotNull Player player, @NotNull ArcadiaMaterial material, int amount) {
        PlayerInventory inventory = player.getInventory();
        if (material.getData().isStackable()) {
            ArcadiaItem item = new ArcadiaItem(material, amount);
            inventory.addItem(item.build());
        } else {
            for (int i = 0; i < amount; i++) {
                ArcadiaItem item = new ArcadiaItem(material);
                inventory.addItem(item.build());
            }
        }
    }
}
