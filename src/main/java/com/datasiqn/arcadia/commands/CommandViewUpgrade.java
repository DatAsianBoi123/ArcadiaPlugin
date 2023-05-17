package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.commands.arguments.ArcadiaArgumentType;
import com.datasiqn.arcadia.guis.ViewUpgradeGUI;
import com.datasiqn.commandcore.commands.builder.ArgumentBuilder;
import com.datasiqn.commandcore.commands.builder.CommandBuilder;

public class CommandViewUpgrade {
    public CommandBuilder getCommand() {
        return new CommandBuilder()
                .then(ArgumentBuilder.argument(ArcadiaArgumentType.UPGRADE, "upgrade")
                        .requiresPlayer()
                        .executes(context -> context.getSource().getPlayer().unwrap().openInventory(new ViewUpgradeGUI(context.getArguments().get(0, ArcadiaArgumentType.UPGRADE).unwrap()).getInventory())));
    }
}
