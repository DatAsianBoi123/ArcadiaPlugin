package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.commands.argument.ArcadiaArgumentType;
import com.datasiqn.commandcore.command.builder.ArgumentBuilder;
import com.datasiqn.commandcore.command.builder.CommandBuilder;

public class CommandGUI {
    private final Arcadia plugin;

    public CommandGUI(Arcadia plugin) {
        this.plugin = plugin;
    }

    public CommandBuilder getCommand() {
        return new CommandBuilder("opengui")
                .permission(ArcadiaPermission.PERMISSION_USE_GUI)
                .description("Opens a custom Arcadia GUI")
                .then(ArgumentBuilder.argument(ArcadiaArgumentType.GUI, "gui")
                        .requiresPlayer()
                        .executes(context -> context.getArguments().get(0, ArcadiaArgumentType.GUI).unwrap().openInventory(context.getSource().getPlayer().unwrap(), plugin)));
    }
}
