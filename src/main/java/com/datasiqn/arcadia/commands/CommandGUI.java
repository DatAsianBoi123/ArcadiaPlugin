package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.commands.arguments.ArcadiaArgumentType;
import com.datasiqn.arcadia.guis.GUIType;
import com.datasiqn.commandcore.commands.Command;
import com.datasiqn.commandcore.commands.builder.ArgumentBuilder;
import com.datasiqn.commandcore.commands.builder.CommandBuilder;
import org.bukkit.entity.Player;

public class CommandGUI {
    private final Arcadia plugin;

    public CommandGUI(Arcadia plugin) {
        this.plugin = plugin;
    }

    public Command getCommand() {
        return new CommandBuilder<>(Player.class)
                .permission(ArcadiaPermission.PERMISSION_USE_GUI)
                .description("Opens a custom Arcadia GUI")
                .then(ArgumentBuilder.<Player, GUIType>argument(ArcadiaArgumentType.GUI, "gui")
                        .executes(context -> context.getSender().openInventory(context.parseArgument(ArcadiaArgumentType.GUI, 0).createInventory(plugin))))
                .build();
    }
}
