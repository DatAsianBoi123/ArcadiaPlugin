package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.commands.arguments.PlayerArgumentType;
import com.datasiqn.arcadia.commands.builder.ArgumentBuilder;
import com.datasiqn.arcadia.commands.builder.CommandBuilder;
import com.datasiqn.arcadia.managers.PlayerManager;
import org.bukkit.entity.Player;

public class CommandHeal {
    private final Arcadia plugin;

    public CommandHeal(Arcadia plugin) {
        this.plugin = plugin;
    }

    public ArcadiaCommand getCommand() {
        return new CommandBuilder<>(Player.class)
                .permission(ArcadiaPermission.PERMISSION_USE_HEAL)
                .description("Heals you or another player")
                .then(ArgumentBuilder.<Player, PlayerManager.PlayerData>argument(new PlayerArgumentType(plugin), "player")
                        .executes(context -> context.parseArgument(new PlayerArgumentType(plugin), 0).playerStats().heal()))
                .executes(sender -> plugin.getPlayerManager().getPlayerData(sender).playerStats().heal())
                .build();
    }
}
