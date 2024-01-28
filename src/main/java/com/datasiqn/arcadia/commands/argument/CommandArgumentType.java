package com.datasiqn.arcadia.commands.argument;

import com.datasiqn.commandcore.CommandCore;
import com.datasiqn.commandcore.argument.ArgumentReader;
import com.datasiqn.commandcore.argument.Arguments;
import com.datasiqn.commandcore.argument.StringArguments;
import com.datasiqn.commandcore.argument.type.ArgumentType;
import com.datasiqn.commandcore.command.Command;
import com.datasiqn.commandcore.command.CommandContext;
import com.datasiqn.commandcore.command.source.CommandSource;
import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import com.google.common.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class CommandArgumentType implements ArgumentType<Function<CommandSource, Result<None, List<String>>>> {
    @Override
    public @NotNull String getName() {
        return "command";
    }

    @Override
    public @NotNull Result<Function<CommandSource, Result<None, List<String>>>, String> parse(@NotNull ArgumentReader reader) {
        String commandName = reader.nextWord();
        Command command = CommandCore.getInstance().getCommandManager().getCommand(commandName, false);
        if (command == null) return Result.error("Invalid command '" + commandName + "'");
        String rest = reader.substring(reader.index() + 1);
        List<String> args = rest.isEmpty() ? Collections.emptyList() : Arrays.asList(rest.split(" "));
        StringArguments arguments = new StringArguments(args);
        reader.jumpTo(reader.size() - 1);
        return Result.ok(source -> command.execute(new CommandContext(source, command, commandName, arguments)));
    }

    @Override
    public @NotNull List<String> getTabComplete(@NotNull CommandContext context) {
        CommandCore commandCore = CommandCore.getInstance();
        List<String> tabComplete = new ArrayList<>(commandCore.getCommandManager().getCommandNames(false));
        Arguments arguments = context.arguments();
        context.source().sendMessage(arguments.toString());
        if (arguments.size() > 0) {
            String[] splitArgs = arguments.getString(arguments.size() - 1).split(" ");
            String commandName = splitArgs[0];
            String[] commandArgs = Arrays.copyOfRange(splitArgs, 1, splitArgs.length);
            Command command = commandCore.getCommandManager().getCommand(commandName, false);
            context.source().sendMessage(commandName);
            context.source().sendMessage(String.join(",", commandArgs));
            if (command == null) return tabComplete;
            CommandContext newContext = new CommandContext(context.source(), command, commandName, new StringArguments(Arrays.asList(commandArgs)));
            tabComplete.addAll(command.tabComplete(newContext).values().stream().map(str -> ". ".repeat(commandArgs.length + 1) + str).toList());
        }
        return tabComplete;
    }

    @Override
    public @NotNull Class<Function<CommandSource, Result<None, List<String>>>> getArgumentClass() {
        //noinspection unchecked
        return (Class<Function<CommandSource, Result<None, List<String>>>>) new TypeToken<Function<CommandSource, Result<None, List<String>>>>() {}.getRawType();
    }
}
