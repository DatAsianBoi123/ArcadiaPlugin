package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.item.ArcadiaItem;
import com.datasiqn.arcadia.item.material.ArcadiaMaterial;
import com.datasiqn.commandcore.argument.annotation.Limit;
import com.datasiqn.commandcore.argument.selector.EntitySelector;
import com.datasiqn.commandcore.command.annotation.*;
import com.datasiqn.commandcore.command.source.CommandSource;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

@CommandDescription(name = "i", description = "Gives you a custom Arcadia item", permission = ArcadiaPermission.PERMISSION_USE_ITEM)
public class CommandItem implements AnnotationCommand {
    @Executor
    public void item(CommandSource source,
                     @Argument(name = "players") @Limit EntitySelector<Player> players,
                     @Argument(name = "item") ArcadiaMaterial item,
                     @Argument(name = "amount") @Optional Integer amount) {
        amount = amount == null ? 1 : amount;
        for (Player player : players.get(source)) {
            PlayerInventory inventory = player.getInventory();
            if (item.getData().isStackable()) {
                inventory.addItem(new ArcadiaItem(item, amount).build());
            } else {
                for (int i = 0; i < amount; i++) {
                    inventory.addItem(new ArcadiaItem(item).build());
                }
            }
        }
    }
}
