package com.datasiqn.arcadia.commands.argument;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.players.PlayerData;
import com.datasiqn.commandcore.argument.ArgumentReader;
import com.datasiqn.commandcore.argument.type.ArgumentType;
import com.datasiqn.commandcore.command.CommandContext;
import com.datasiqn.resultapi.Result;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

class PlayerArgumentType implements ArgumentType<PlayerData> {
    @Override
    public @NotNull Result<PlayerData, String> parse(@NotNull ArgumentReader reader) {
        return PLAYER.parse(reader).map(player -> JavaPlugin.getPlugin(Arcadia.class).getPlayerManager().getPlayerData(player));
    }

    @Override
    public @NotNull List<String> getTabComplete(@NotNull CommandContext context) {
        return PLAYER.getTabComplete(context);
    }
}
