package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.commands.argument.ArcadiaArgumentType;
import com.datasiqn.arcadia.menu.handlers.UpgradeMenuHandler;
import com.datasiqn.commandcore.commands.builder.ArgumentBuilder;
import com.datasiqn.commandcore.commands.builder.CommandBuilder;
import com.datasiqn.menuapi.MenuApi;
import org.bukkit.inventory.Inventory;

public class CommandViewUpgrade {
    public CommandBuilder getCommand() {
        return new CommandBuilder()
                .then(ArgumentBuilder.argument(ArcadiaArgumentType.UPGRADE, "upgrade")
                        .requiresPlayer()
                        .executes(context -> {
                            UpgradeMenuHandler gui = new UpgradeMenuHandler(context.getArguments().get(0, ArcadiaArgumentType.UPGRADE).unwrap());
                            Inventory inventory = gui.createInventory();
                            MenuApi.getInstance().getMenuManager().registerHandler(inventory, gui);
                            context.getSource().getPlayer().unwrap().openInventory(inventory);
                        }));
    }
}
