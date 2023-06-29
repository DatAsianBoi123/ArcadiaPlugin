package com.datasiqn.arcadia.commands.argument;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.dungeon.DungeonInstance;
import com.datasiqn.arcadia.managers.DungeonManager;
import com.datasiqn.commandcore.argument.type.SimpleArgumentType;
import com.datasiqn.commandcore.command.CommandContext;
import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

class DungeonArgumentType implements SimpleArgumentType<DungeonInstance> {
    private final DungeonManager dungeonManager = JavaPlugin.getPlugin(Arcadia.class).getDungeonManager();

    @Override
    public @NotNull String getTypeName() {
        return "dungeon";
    }

    @Override
    public @NotNull Result<DungeonInstance, None> parseWord(String word) {
        return Result.ofNullable(dungeonManager.getCreatedDungeon(word), None.NONE);
    }

    @Override
    public @NotNull List<String> getTabComplete(@NotNull CommandContext context) {
        return dungeonManager.getAllDungeonInstances().stream().map(DungeonInstance::getId).toList();
    }
}
