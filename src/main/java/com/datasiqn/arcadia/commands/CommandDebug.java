package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.players.PlayerData;
import com.datasiqn.commandcore.arguments.ArgumentType;
import com.datasiqn.commandcore.commands.builder.ArgumentBuilder;
import com.datasiqn.commandcore.commands.builder.CommandBuilder;
import org.bukkit.entity.Player;

public class CommandDebug {
    private final Arcadia plugin;

    public CommandDebug(Arcadia plugin) {
        this.plugin = plugin;
    }

    public CommandBuilder getCommand() {
        return new CommandBuilder()
                .permission(ArcadiaPermission.PERMISSION_USE_DEBUG)
                .description("Changes if you're in debug mode or not")
                .then(ArgumentBuilder.argument(ArgumentType.BOOLEAN, "mode")
                        .requiresPlayer()
                        .executes(context -> setDebugMode(context.getArguments().get(0, ArgumentType.BOOLEAN).unwrap(), context.getSource().getPlayer().unwrap())))
                .requiresPlayer()
                .executes(context -> toggleDebugMode(context.getSource().getPlayer().unwrap()));
    }

    private void toggleDebugMode(Player player) {
        setDebugMode(!plugin.getPlayerManager().getPlayerData(player).inDebugMode(), player);
    }

    private void setDebugMode(boolean mode, Player player) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        playerData.setDebugMode(mode);
        playerData.getPlayer().sendMessage("Set debug mode to: " + mode);
    }
}
