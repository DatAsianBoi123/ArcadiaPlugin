package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.commandcore.commands.Command;
import com.datasiqn.commandcore.commands.builder.CommandBuilder;
import org.bukkit.entity.Player;

public class CommandLobby {
    private final Arcadia plugin;

    public CommandLobby(Arcadia plugin) {
        this.plugin = plugin;
    }

    public Command getCommand() {
        return new CommandBuilder<>(Player.class)
                .permission(ArcadiaPermission.PERMISSION_USE_LOBBY)
                .description("Sends you to the lobby")
                .executes(plugin.getDungeonManager()::leaveDungeon)
                .build();
    }
}
