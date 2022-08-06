package com.datasiqn.arcadia.managers;

import com.datasiqn.arcadia.commands.ArcadiaCommand;
import org.jetbrains.annotations.Unmodifiable;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final Map<String, ArcadiaCommand> commandMap = new HashMap<>();

    public void registerCommand(String name, ArcadiaCommand command) {
        commandMap.put(name, command);
    }

    public ArcadiaCommand getCommand(String name) {
        return commandMap.get(name);
    }

    public boolean hasCommand(String name) {
        return commandMap.containsKey(name);
    }

    @Unmodifiable
    public Map<String, ArcadiaCommand> allCommands() {
        return Map.copyOf(commandMap);
    }
}
