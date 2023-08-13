package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.commands.argument.ArcadiaArgumentType;
import com.datasiqn.commandcore.command.builder.ArgumentBuilder;
import com.datasiqn.commandcore.command.builder.CommandBuilder;

public class CommandHeal {
    private final Arcadia plugin;

    public CommandHeal(Arcadia plugin) {
        this.plugin = plugin;
    }

    public CommandBuilder getCommand() {
        return new CommandBuilder("heal")
                .permission(ArcadiaPermission.PERMISSION_USE_HEAL)
                .description("Heals you or another player")
                .then(ArgumentBuilder.argument(ArcadiaArgumentType.PLAYER, "player")
                        .executes(context -> context.getArguments().get(0, ArcadiaArgumentType.PLAYER).heal()))
                .requiresPlayer()
                .executes(context -> plugin.getPlayerManager().getPlayerData(context.getSource().getPlayer()).heal());
    }
}
