package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.commands.arguments.Arguments;
import com.datasiqn.arcadia.players.ArcadiaSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface ArcadiaCommand {
    boolean execute(@NotNull ArcadiaSender<?> sender, @NotNull Arguments args);

    @Nullable
    String getPermissionString();

    @NotNull
    String getDescription();

    List<String> getUsages();

    @NotNull
    default List<String> tabComplete(@NotNull ArcadiaSender<?> sender, @NotNull Arguments args) {
        return new ArrayList<>();
    }
}
