package com.datasiqn.arcadia.commands.builder;

import com.datasiqn.arcadia.commands.ArcadiaCommand;
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
            public boolean execute(@NotNull ArcadiaSender<?> sender, @NotNull Arguments args) {
                if (!senderClass.isInstance(sender.get())) {
                    sender.sendMessage("You cannot send this command");
                    return true;
                }

                //noinspection unchecked
                ArcadiaSender<S> castedSender = (ArcadiaSender<S>) sender;

                if (args.size() >= 1) {
                    if (nodes.isEmpty()) return false;

                    CommandNode<S, ?> currentNode = findCurrentNode(args, args.size());
                    if (currentNode == null) return false;
                    Optional<String> optionalArg = args.get(args.size() - 1, ArgumentType.STRING);
                    if (optionalArg.isEmpty()) return true;
                    currentNode.executeWith(castedSender, args.asList());
                    return true;
                }

                if (executor == null) return false;
                executor.accept(castedSender);
                return true;
            }

            @Override
            public @NotNull List<String> tabComplete(@NotNull ArcadiaSender<?> sender, @NotNull Arguments args) {
                if (args.size() >= 1) {
                    List<String> tabComplete = new ArrayList<>();
                    CommandNode<S, ?> currentNode = findCurrentNode(args, args.size() - 1);
                    Set<CommandNode<S, ?>> tabCompleteNodes;
                    if (args.size() == 1) {
                        tabCompleteNodes = nodes;
                    } else if (currentNode == null) {
                        return ArcadiaCommand.super.tabComplete(sender, args);
                    } else {
                        tabCompleteNodes = currentNode.getChildren();
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
                for (CommandNode<S, ?> node : nodes) {
                    usages.addAll(node.getUsages());
                }
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

    private @Nullable CommandNode<S, ?> findCurrentNode(@NotNull Arguments args, int iterations) {
        Set<CommandNode<S, ?>> nodeSet = nodes;
        CommandNode<S, ?> currentNode = null;
        for (int i = 0; i < iterations; i++) {
            Optional<CommandNode<S, ?>> commandNode = checkAt(args, i, nodeSet);
            if (commandNode.isEmpty()) return null;
            nodeSet = commandNode.get().getChildren();
            currentNode = commandNode.get();
        }
        return currentNode;
    }
}
