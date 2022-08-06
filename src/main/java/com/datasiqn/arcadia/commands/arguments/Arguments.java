package com.datasiqn.arcadia.commands.arguments;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Optional;

public class Arguments {
    protected final List<String> allArguments;

    public Arguments(List<String> args) {
        allArguments = args;
    }

    public int size() {
        return allArguments.size();
    }

    @NotNull
    public <T> Optional<T> get(int i, ArgumentType<T> type) {
        if (i >= allArguments.size()) return Optional.empty();
        return type.fromString(allArguments.get(i));
    }

    public @Unmodifiable @NotNull List<String> asList() {
        return List.copyOf(allArguments);
    }
}
