package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.commands.argument.ArcadiaArgumentType;
import com.datasiqn.arcadia.item.ArcadiaItem;
import com.datasiqn.arcadia.item.material.ArcadiaMaterial;
import com.datasiqn.commandcore.argument.selector.EntitySelector;
import com.datasiqn.commandcore.argument.type.ArgumentType;
import com.datasiqn.commandcore.command.builder.ArgumentBuilder;
import com.datasiqn.commandcore.command.builder.CommandBuilder;
import com.datasiqn.commandcore.command.source.CommandSource;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

public class CommandItem {
    public CommandBuilder getCommand() {
        return new CommandBuilder("i")
                .permission(ArcadiaPermission.PERMISSION_USE_ITEM)
                .description("Gives you a custom Arcadia Item")
                .then(ArgumentBuilder.argument(ArgumentType.PLAYERS, "player")
                        .then(ArgumentBuilder.argument(ArcadiaArgumentType.ITEM, "item")
                                .then(ArgumentBuilder.argument(ArgumentType.boundedNumber(int.class, 1), "amount")
                                        .executes((context, source, arguments) -> giveItem(source, arguments.get(0, ArgumentType.PLAYERS), arguments.get(1, ArcadiaArgumentType.ITEM), arguments.get(2, ArgumentType.boundedNumber(int.class, 1)))))
                                .executes((context, source, arguments) -> giveItem(source, arguments.get(0, ArgumentType.PLAYERS), arguments.get(1, ArcadiaArgumentType.ITEM), 1))));
    }

    private void giveItem(CommandSource source, @NotNull EntitySelector<Player> entitySelector, @NotNull ArcadiaMaterial material, int amount) {
        for (Player player : entitySelector.get(source)) {
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
}
