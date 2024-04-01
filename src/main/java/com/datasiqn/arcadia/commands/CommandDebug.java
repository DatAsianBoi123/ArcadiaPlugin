package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.player.PlayerData;
import com.datasiqn.commandcore.command.annotation.*;
import org.bukkit.entity.Player;

@CommandDescription(name = "debug", description = "Changes if you're in debug mode or not", permission = ArcadiaPermission.PERMISSION_USE_DEBUG)
public class CommandDebug implements AnnotationCommand {
    private final Arcadia plugin;

    public CommandDebug(Arcadia plugin) {
        this.plugin = plugin;
    }

    @Executor
    public void debug(Player player,
                      @Argument(name = "mode") @Optional Boolean mode) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        playerData.setDebugMode(mode == null ? !playerData.inDebugMode() : mode);
        playerData.getSender().sendMessage("Set debug mode to: " + playerData.inDebugMode());
    }
}
