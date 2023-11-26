package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.player.PlayerData;
import com.datasiqn.commandcore.argument.type.ArgumentType;
import com.datasiqn.commandcore.command.builder.ArgumentBuilder;
import com.datasiqn.commandcore.command.builder.CommandBuilder;
import org.bukkit.entity.Player;

public class CommandDebug {
    private final Arcadia plugin;

    public CommandDebug(Arcadia plugin) {
        this.plugin = plugin;
    }

    public CommandBuilder getCommand() {
        return new CommandBuilder("debug")
                .permission(ArcadiaPermission.PERMISSION_USE_DEBUG)
                .description("Changes if you're in debug mode or not")
                .then(ArgumentBuilder.argument(ArgumentType.BOOLEAN, "mode")
                        .requiresPlayer()
                        .executes((context, source, arguments) -> setDebugMode(arguments.get(0, ArgumentType.BOOLEAN), source.getPlayer())))
                .requiresPlayer()
                .executes((context, source, arguments) -> toggleDebugMode(source.getPlayer()));
    }

    private void toggleDebugMode(Player player) {
        setDebugMode(!plugin.getPlayerManager().getPlayerData(player).inDebugMode(), player);
    }

    private void setDebugMode(boolean mode, Player player) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        playerData.setDebugMode(mode);
        playerData.getSender().sendMessage("Set debug mode to: " + mode);
    }
}
