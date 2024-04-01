package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.menu.handlers.UpgradeMenuHandler;
import com.datasiqn.arcadia.upgrade.UpgradeType;
import com.datasiqn.commandcore.command.annotation.AnnotationCommand;
import com.datasiqn.commandcore.command.annotation.Argument;
import com.datasiqn.commandcore.command.annotation.CommandDescription;
import com.datasiqn.commandcore.command.annotation.Executor;
import com.datasiqn.menuapi.MenuApi;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@CommandDescription(name = "viewupgrade", description = "Views an upgrade", permission = ArcadiaPermission.PERMISSION_USE_UPGRADE)
public class CommandViewUpgrade implements AnnotationCommand {
    @Executor
    public void viewUpgrade(Player player,
                            @Argument(name = "upgrade") UpgradeType upgradeType) {
        UpgradeMenuHandler gui = new UpgradeMenuHandler(upgradeType);
        Inventory inventory = gui.createInventory();
        MenuApi.getInstance().getMenuManager().registerHandler(inventory, gui);
        player.openInventory(inventory);
    }
}
