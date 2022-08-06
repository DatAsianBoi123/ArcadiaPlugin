package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.commands.arguments.ArgumentType;
import com.datasiqn.arcadia.commands.builder.ArgumentBuilder;
import com.datasiqn.arcadia.commands.builder.CommandBuilder;
import org.bukkit.entity.Player;

public class CommandDebug {
    private final Arcadia plugin;

    public CommandDebug(Arcadia plugin) {
        this.plugin = plugin;
    }

    public ArcadiaCommand getCommand() {
        return new CommandBuilder<>(Player.class)
                .permission(ArcadiaPermission.PERMISSION_USE_DEBUG)
                .description("Changes if you're in debug mode or not")
                .then(ArgumentBuilder.<Player, Boolean>argument(ArgumentType.BOOLEAN, "mode")
                        .executes(context -> {
                            boolean debugMode = context.parseArgument(ArgumentType.BOOLEAN, 0);
                            plugin.setDebugMode(context.sender().get().getUniqueId(), debugMode);
                            context.sender().sendMessage("Set debug mode to: " + debugMode);
                        }))
                .executes(sender -> {
                    boolean debugMode = !plugin.inDebugMode(sender.get().getUniqueId());
                    plugin.setDebugMode(sender.get().getUniqueId(), debugMode);
                    sender.sendMessage("Set debug mode to: " + debugMode);
                })
                .build();
    }
}
