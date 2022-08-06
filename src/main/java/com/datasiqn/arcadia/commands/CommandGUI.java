package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.commands.arguments.ArgumentType;
import com.datasiqn.arcadia.commands.builder.ArgumentBuilder;
import com.datasiqn.arcadia.commands.builder.CommandBuilder;
import com.datasiqn.arcadia.guis.GUIType;
import org.bukkit.entity.Player;

public class CommandGUI {
    public ArcadiaCommand getCommand() {
        return new CommandBuilder<>(Player.class)
                .permission(ArcadiaPermission.PERMISSION_USE_GUI)
                .description("Opens a custom Arcadia GUI")
                .then(ArgumentBuilder.<Player, GUIType>argument(ArgumentType.GUI, "gui")
                        .executes(context -> context.sender().get().openInventory(context.parseArgument(ArgumentType.GUI, 0).getInventory())))
                .build();
    }
}
