package com.datasiqn.arcadia.commands.builder;

import com.datasiqn.arcadia.players.ArcadiaSender;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public abstract class CommandNode<S extends CommandSender, This extends CommandNode<S, This>> {
    protected final Set<CommandNode<S, ?>> children = new HashSet<>();

    protected Consumer<CommandContext<S>> executor;

    public final @NotNull This then(CommandNode<S, ?> node) {
        children.add(node);
        return getThis();
    }

    public final @NotNull This executes(Consumer<CommandContext<S>> executor) {
        this.executor = executor;
        return getThis();
    }

    public final boolean executeWith(ArcadiaSender<S> sender, List<String> args) {
        if (executor == null) return false;
        executor.accept(new CommandContext<>(sender, args));
        return true;
    }

    @NotNull
    public List<String> getTabComplete(int index) {
        return new ArrayList<>();
    }

    @Contract(" -> new")
    public final @NotNull @Unmodifiable Set<CommandNode<S, ?>> getChildren() {
        return Set.copyOf(children);
    }

    public abstract boolean isApplicable(String arg);

    protected List<String> getUsages(boolean isOptional) {
        List<String> usages = new ArrayList<>();
        if (executor != null) usages.add(getUsageArgument(isOptional));
        AtomicBoolean hasOptional = new AtomicBoolean(false);
        children.forEach(node -> {
            if (node.executor != null) hasOptional.set(true);
            usages.addAll(node.getUsages(node.executor != null).stream().map(str -> getUsageArgument(node.executor != null) + " " + str).toList());
        });
        if (executor != null && hasOptional.get() && canBeOptional()) usages.remove(0);
        return usages;
    }

    protected abstract String getUsageArgument(boolean isOptional);

    protected boolean canBeOptional() {
        return false;
    }

    @NotNull
    protected abstract This getThis();
}
