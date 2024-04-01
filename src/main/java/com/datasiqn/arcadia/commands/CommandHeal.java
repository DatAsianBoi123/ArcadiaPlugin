package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.managers.PlayerManager;
import com.datasiqn.arcadia.player.ArcadiaSender;
import com.datasiqn.commandcore.argument.annotation.Limit;
import com.datasiqn.commandcore.argument.selector.EntitySelector;
import com.datasiqn.commandcore.command.annotation.AnnotationCommand;
import com.datasiqn.commandcore.command.annotation.Argument;
import com.datasiqn.commandcore.command.annotation.CommandDescription;
import com.datasiqn.commandcore.command.annotation.Executor;
import com.datasiqn.commandcore.command.source.CommandSource;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandDescription(name = "heal", description = "Heals you or another player", permission = ArcadiaPermission.PERMISSION_USE_HEAL)
public class CommandHeal implements AnnotationCommand {
    private final Arcadia plugin;

    public CommandHeal(Arcadia plugin) {
        this.plugin = plugin;
    }

    @Executor
    public void heal(CommandSource source,
                     @Argument(name = "player") @Limit EntitySelector<Player> players) {
        ArcadiaSender<CommandSender> sender = new ArcadiaSender<>(source.getSender());
        PlayerManager playerManager = plugin.getPlayerManager();
        for (Player player : players.get(source)) {
            playerManager.getPlayerData(player).heal();
            sender.sendMessage("Healed " + player.getName());
        }
    }
}
