package com.datasiqn.arcadia.commands.argument;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.player.PlayerData;
import com.datasiqn.commandcore.argument.ArgumentReader;
import com.datasiqn.commandcore.argument.type.ArgumentType;
import com.datasiqn.commandcore.command.CommandContext;
import com.datasiqn.resultapi.Result;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

class PlayerArgumentType implements ArgumentType<PlayerData> {
    @Override
    public @NotNull String getName() {
        return "player";
    }

    @Override
    public @NotNull Result<PlayerData, String> parse(@NotNull ArgumentReader reader) {
        return ONLINE_PLAYER.parse(reader).map(player -> JavaPlugin.getPlugin(Arcadia.class).getPlayerManager().getPlayerData(player));
    }

    @Override
    public @NotNull List<String> getTabComplete(@NotNull CommandContext context) {
        return ONLINE_PLAYER.getTabComplete(context);
    }

    @Override
    public @NotNull Class<PlayerData> getArgumentClass() {
        return PlayerData.class;
    }
}
