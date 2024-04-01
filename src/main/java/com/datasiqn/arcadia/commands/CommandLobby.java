package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.commandcore.command.annotation.AnnotationCommand;
import com.datasiqn.commandcore.command.annotation.CommandDescription;
import com.datasiqn.commandcore.command.annotation.Executor;
import org.bukkit.entity.Player;

@CommandDescription(name = "lobby", description = "Sends you to the lobby", permission = ArcadiaPermission.PERMISSION_USE_LOBBY)
public class CommandLobby implements AnnotationCommand {
    private final Arcadia plugin;

    public CommandLobby(Arcadia plugin) {
        this.plugin = plugin;
    }

    @Executor
    public void lobby(Player player) {
        plugin.getDungeonManager().leaveDungeon(player);
    }
}
