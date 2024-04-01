package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.menu.MenuType;
import com.datasiqn.commandcore.command.annotation.AnnotationCommand;
import com.datasiqn.commandcore.command.annotation.Argument;
import com.datasiqn.commandcore.command.annotation.CommandDescription;
import com.datasiqn.commandcore.command.annotation.Executor;
import org.bukkit.entity.Player;

@CommandDescription(name = "opengui", description = "Opens a custom Arcadia gui", permission = ArcadiaPermission.PERMISSION_USE_GUI)
public class CommandGUI implements AnnotationCommand {
    private final Arcadia plugin;

    public CommandGUI(Arcadia plugin) {
        this.plugin = plugin;
    }

    @Executor
    public void openGui(Player player,
                        @Argument(name = "gui") MenuType menuType) {
        menuType.openInventory(player, plugin);
    }
}
