package com.datasiqn.arcadia.util.lorebuilder;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.List;

public interface Lore {
    Lore EMPTY = new EmptyLore();

    @Unmodifiable
    List<String> asStringList();

    default boolean isEmpty() {
        return asStringList().isEmpty();
    }

    default void addTo(@NotNull List<String> list) {
        list.addAll(asStringList());
    }
    default void addTo(int index, @NotNull List<String> list) {
        list.addAll(index, asStringList());
    }

    @Contract(value = "_ -> new", pure = true)
    static @NotNull Lore of(String @NotNull ... lore) {
        return new LoreOf(Arrays.stream(lore).map(s -> ChatColor.GRAY + s).toList());
    }
}
