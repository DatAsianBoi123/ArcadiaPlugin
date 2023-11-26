package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.commandcore.command.builder.CommandBuilder;

public class CommandLobby {
    private final Arcadia plugin;

    public CommandLobby(Arcadia plugin) {
        this.plugin = plugin;
    }

    public CommandBuilder getCommand() {
        return new CommandBuilder("lobby")
                .permission(ArcadiaPermission.PERMISSION_USE_LOBBY)
                .description("Sends you to the lobby")
                .requiresPlayer()
                .executes((context, source, arguments) -> plugin.getDungeonManager().leaveDungeon(source.getPlayer()));
    }
}
