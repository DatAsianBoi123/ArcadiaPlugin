package com.datasiqn.arcadia.commands.arguments;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.commands.ArcadiaCommand;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommandArgumentType implements ArgumentType<ArcadiaCommand> {
    private final Arcadia plugin;

    public CommandArgumentType(Arcadia plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull Optional<ArcadiaCommand> fromString(@NotNull String str) {
        return Optional.ofNullable(plugin.getCommandManager().getCommand(str));
    }

    @Override
    public @NotNull List<String> all() {
        return new ArrayList<>(plugin.getCommandManager().allCommands().keySet());
    }
}
