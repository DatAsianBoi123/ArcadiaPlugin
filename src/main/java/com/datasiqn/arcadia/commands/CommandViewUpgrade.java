package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.commands.argument.ArcadiaArgumentType;
import com.datasiqn.arcadia.menu.handlers.UpgradeMenuHandler;
import com.datasiqn.commandcore.command.builder.ArgumentBuilder;
import com.datasiqn.commandcore.command.builder.CommandBuilder;
import com.datasiqn.menuapi.MenuApi;
import org.bukkit.inventory.Inventory;

public class CommandViewUpgrade {
    public CommandBuilder getCommand() {
        return new CommandBuilder("viewupgrade")
                .permission(ArcadiaPermission.PERMISSION_USE_UPGRADE)
                .description("Views an upgrade")
                .then(ArgumentBuilder.argument(ArcadiaArgumentType.UPGRADE, "upgrade")
                        .requiresPlayer()
                        .executes((context, source, arguments) -> {
                            UpgradeMenuHandler gui = new UpgradeMenuHandler(arguments.get(0, ArcadiaArgumentType.UPGRADE));
                            Inventory inventory = gui.createInventory();
                            MenuApi.getInstance().getMenuManager().registerHandler(inventory, gui);
                            source.getPlayer().openInventory(inventory);
                        }));
    }
}
