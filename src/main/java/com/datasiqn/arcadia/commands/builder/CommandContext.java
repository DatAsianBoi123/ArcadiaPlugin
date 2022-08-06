package com.datasiqn.arcadia.commands.builder;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.commands.arguments.ArgumentType;
import com.datasiqn.arcadia.players.ArcadiaSender;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record CommandContext<S extends CommandSender>(ArcadiaSender<S> sender, List<String> arguments) {
    public <T> T parseArgument(@NotNull ArgumentType<T> type, int index) {
        return type.fromString(arguments.get(index)).orElseThrow();
    }
}
