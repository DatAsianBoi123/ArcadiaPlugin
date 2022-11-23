package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.commandcore.arguments.ArgumentType;
import com.datasiqn.commandcore.commands.builder.ArgumentBuilder;
import com.datasiqn.commandcore.commands.builder.CommandBuilder;

public class CommandHeal {
    private final Arcadia plugin;

    public CommandHeal(Arcadia plugin) {
        this.plugin = plugin;
    }

    public CommandBuilder getCommand() {
        return new CommandBuilder()
                .permission(ArcadiaPermission.PERMISSION_USE_HEAL)
                .description("Heals you or another player")
                .then(ArgumentBuilder.argument(ArgumentType.PLAYER, "player")
                        .executes(context -> plugin.getPlayerManager().getPlayerData(context.getArguments().get(0, ArgumentType.PLAYER).unwrap()).heal()))
                .requiresPlayer()
                .executes(context -> plugin.getPlayerManager().getPlayerData(context.getSource().getPlayer().unwrap()).heal());
    }
}
