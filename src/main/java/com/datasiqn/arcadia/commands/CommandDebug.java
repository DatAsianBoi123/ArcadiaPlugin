package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.players.PlayerData;
import com.datasiqn.commandcore.arguments.ArgumentType;
import com.datasiqn.commandcore.commands.Command;
import com.datasiqn.commandcore.commands.builder.ArgumentBuilder;
import com.datasiqn.commandcore.commands.builder.CommandBuilder;
import org.bukkit.entity.Player;

public class CommandDebug {
    private final Arcadia plugin;

    public CommandDebug(Arcadia plugin) {
        this.plugin = plugin;
    }

    public Command getCommand() {
        return new CommandBuilder<>(Player.class)
                .permission(ArcadiaPermission.PERMISSION_USE_DEBUG)
                .description("Changes if you're in debug mode or not")
                .then(ArgumentBuilder.<Player, Boolean>argument(ArgumentType.BOOLEAN, "mode")
                        .executes(context -> {
                            boolean debugMode = context.parseArgument(ArgumentType.BOOLEAN, 0);
                            PlayerData playerData = plugin.getPlayerManager().getPlayerData(context.getSender());
                            playerData.setDebugMode(debugMode);
                            playerData.getPlayer().sendMessage("Set debug mode to: " + debugMode);
                        }))
                .executes(sender -> {
                    PlayerData playerData = plugin.getPlayerManager().getPlayerData(sender);
                    playerData.toggleDebugMode();
                    playerData.getPlayer().sendMessage("Set debug mode to: " + playerData.inDebugMode());
                })
                .build();
    }
}
