package com.datasiqn.arcadia.commands.builder;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LiteralBuilder<S extends CommandSender> extends CommandNode<S, LiteralBuilder<S>> {
    private final String literal;

    private LiteralBuilder(String literal) {
        this.literal = literal;
    }

    @Override
    public boolean isApplicable(String arg) {
        return literal.equals(arg);
    }

    @Override
    public @NotNull List<String> getTabComplete(int index) {
        if (index == 0) return new ArrayList<>(Collections.singletonList(literal));
        return super.getTabComplete(index);
    }

    @Override
    protected String getUsageArgument() {
        return literal;
    }

    @Override
    protected @NotNull LiteralBuilder<S> getThis() {
        return this;
    }

    @Contract("_ -> new")
    public static <S extends CommandSender> @NotNull LiteralBuilder<S> literal(String literal) {
        return new LiteralBuilder<>(literal);
    }
}
