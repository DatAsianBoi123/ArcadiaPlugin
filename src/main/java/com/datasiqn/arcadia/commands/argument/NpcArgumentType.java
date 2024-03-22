package com.datasiqn.arcadia.commands.argument;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.npc.CreatedNpc;
import com.datasiqn.commandcore.argument.ArgumentReader;
import com.datasiqn.commandcore.argument.type.ArgumentType;
import com.datasiqn.commandcore.command.CommandContext;
import com.datasiqn.resultapi.Result;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NpcArgumentType implements ArgumentType<CreatedNpc> {
    @Override
    public @NotNull String getName() {
        return "npc";
    }

    @Override
    public @NotNull Result<CreatedNpc, String> parse(@NotNull ArgumentReader reader) {
        return ArgumentType.boundedNumber(long.class, 0L).parse(reader)
                .andThen(id -> {
                    Arcadia plugin = JavaPlugin.getPlugin(Arcadia.class);
                    return Result.ofNullable(plugin.getNpcManager().getNpc(id), "No NPC exists with the id " + id);
                });
    }

    @Override
    public @NotNull List<String> getTabComplete(@NotNull CommandContext context) {
        Arcadia plugin = JavaPlugin.getPlugin(Arcadia.class);
        return plugin.getNpcManager().ids().stream().map(Object::toString).toList();
    }

    @Override
    public @NotNull Class<CreatedNpc> getArgumentClass() {
        return CreatedNpc.class;
    }
}
