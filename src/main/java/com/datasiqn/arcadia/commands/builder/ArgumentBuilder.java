package com.datasiqn.arcadia.commands.builder;

import com.datasiqn.arcadia.commands.arguments.ArgumentType;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ArgumentBuilder<S extends CommandSender, T> extends CommandNode<S, ArgumentBuilder<S, T>> {
    private final ArgumentType<T> type;
    private final String argName;

    private ArgumentBuilder(ArgumentType<T> type, String argName) {
        this.type = type;
        this.argName = argName;
    }

    @Override
    public @NotNull List<String> getTabComplete(int index) {
        if (index == 0) return type.all();
        return super.getTabComplete(index);
    }

    @Override
    public boolean isApplicable(String arg) {
        return type.fromString(arg).isPresent();
    }

    @Override
    protected String getUsageArgument() {
        return "<" + argName + ">";
    }

    @Override
    protected @NotNull ArgumentBuilder<S, T> getThis() {
        return this;
    }

    @Contract("_, _ -> new")
    public static <S extends CommandSender, T> @NotNull ArgumentBuilder<S, T> argument(ArgumentType<T> type, String argName) {
        return new ArgumentBuilder<>(type, argName);
    }
}
