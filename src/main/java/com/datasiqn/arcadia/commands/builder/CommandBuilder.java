package com.datasiqn.arcadia.commands.builder;

import com.datasiqn.arcadia.commands.ArcadiaCommand;
import com.datasiqn.arcadia.commands.CommandOutput;
import com.datasiqn.arcadia.commands.arguments.ArgumentType;
import com.datasiqn.arcadia.commands.arguments.Arguments;
import com.datasiqn.arcadia.players.ArcadiaSender;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class CommandBuilder<S extends CommandSender> {
    private final Set<CommandNode<S, ?>> nodes = new HashSet<>();
    private final Class<S> senderClass;

    private String permission;
    private Consumer<ArcadiaSender<S>> executor;
    private String description = "Cool description";

    public CommandBuilder(Class<S> senderClass) {
        this.senderClass = senderClass;
    }

    public CommandBuilder<S> permission(String permission) {
        this.permission = permission;
        return this;
    }

    public CommandBuilder<S> description(String description) {
        this.description = description;
        return this;
    }

    public CommandBuilder<S> then(CommandNode<S, ?> node) {
        nodes.add(node);
        return this;
    }

    public CommandBuilder<S> executes(Consumer<ArcadiaSender<S>> executor) {
        this.executor = executor;
        return this;
    }

    public ArcadiaCommand build() {
        return new ArcadiaCommand() {
            @Override
            public CommandOutput execute(@NotNull ArcadiaSender<?> sender, @NotNull Arguments args) {
                if (!senderClass.isInstance(sender.get())) {
                    sender.sendMessage("You cannot send this command");
                    return CommandOutput.success();
                }

                //noinspection unchecked
                ArcadiaSender<S> castedSender = (ArcadiaSender<S>) sender;

                if (args.size() >= 1) {
                    if (nodes.isEmpty()) return CommandOutput.failure("Invalid argument size: expected 1 but got " + args.size());

                    FoundNode<S> currentNode = findCurrentNode(args, args.size());
                    Optional<String> optionalArg = args.get(args.size() - 1, ArgumentType.STRING);
                    if (optionalArg.isEmpty()) return CommandOutput.failure("An unexpected error occurred");
                    if (currentNode.node == null) {
                        return CommandOutput.failure("Invalid argument " + optionalArg.get());
                    }
                    boolean hasExecutor = currentNode.node.executeWith(castedSender, args.asList());
                    return hasExecutor ? CommandOutput.success() : CommandOutput.failure();
                }

                if (executor == null) return CommandOutput.failure("Invalid argument size: 1");
                executor.accept(castedSender);
                return CommandOutput.success();
            }

            @Override
            public @NotNull List<String> tabComplete(@NotNull ArcadiaSender<?> sender, @NotNull Arguments args) {
                if (args.size() >= 1) {
                    List<String> tabComplete = new ArrayList<>();
                    FoundNode<S> currentNode = findCurrentNode(args, args.size() - 1);
                    Set<CommandNode<S, ?>> tabCompleteNodes;
                    if (args.size() == 1) {
                        tabCompleteNodes = nodes;
                    } else if (currentNode.node == null) {
                        return ArcadiaCommand.super.tabComplete(sender, args);
                    } else {
                        tabCompleteNodes = currentNode.node.getChildren();
                    }
                    tabCompleteNodes.forEach(node -> tabComplete.addAll(node.getTabComplete(0)));
                    return tabComplete;
                }

                return ArcadiaCommand.super.tabComplete(sender, args);
            }

            @Override
            public @Nullable String getPermissionString() {
                return permission;
            }

            @Override
            public @NotNull String getDescription() {
                return description;
            }

            @Override
            public List<String> getUsages() {
                List<String> usages = new ArrayList<>();
                if (executor != null) usages.add("");
                boolean hasOptional = false;
                for (CommandNode<S, ?> node : nodes) {
                    if (node.executor != null) hasOptional = true;
                    usages.addAll(node.getUsages(executor != null));
                }
                if (executor != null && hasOptional) usages.remove(0);
                return usages;
            }
        };
    }

    private Optional<CommandNode<S, ?>> checkAt(@NotNull Arguments args, int index, Collection<CommandNode<S, ?>> nodes) {
        Optional<String> optionalArg = args.get(index, ArgumentType.STRING);
        if (optionalArg.isEmpty()) return Optional.empty();
        Optional<CommandNode<S, ?>> nodeOptional = nodes.stream().filter(node -> node.isApplicable(optionalArg.get())).findFirst();
        if (nodeOptional.isEmpty()) return Optional.empty();
        return nodeOptional;
    }

    private @NotNull FoundNode<S> findCurrentNode(@NotNull Arguments args, int iterations) {
        Set<CommandNode<S, ?>> nodeSet = nodes;
        CommandNode<S, ?> currentNode = null;
        for (int i = 0; i < iterations; i++) {
            Optional<CommandNode<S, ?>> commandNode = checkAt(args, i, nodeSet);
            if (commandNode.isEmpty()) return new FoundNode<>(null, i);
            nodeSet = commandNode.get().getChildren();
            currentNode = commandNode.get();
        }
        return new FoundNode<>(currentNode, iterations);
    }

    private record FoundNode<S extends CommandSender>(@Nullable CommandNode<S, ?> node, int lastIndex) { }
}
