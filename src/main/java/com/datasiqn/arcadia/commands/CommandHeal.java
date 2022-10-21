package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.commandcore.arguments.ArgumentType;
import com.datasiqn.commandcore.commands.Command;
import com.datasiqn.commandcore.commands.builder.ArgumentBuilder;
import com.datasiqn.commandcore.commands.builder.CommandBuilder;
import org.bukkit.entity.Player;

public class CommandHeal {
    private final Arcadia plugin;

    public CommandHeal(Arcadia plugin) {
        this.plugin = plugin;
    }

    public Command getCommand() {
        return new CommandBuilder<>(Player.class)
                .permission(ArcadiaPermission.PERMISSION_USE_HEAL)
                .description("Heals you or another player")
                .then(ArgumentBuilder.<Player, Player>argument(ArgumentType.PLAYER, "player")
                        .executes(context -> plugin.getPlayerManager().getPlayerData(context.parseArgument(ArgumentType.PLAYER, 0)).heal()))
                .executes(sender -> plugin.getPlayerManager().getPlayerData(sender).heal())
                .build();
    }
}
