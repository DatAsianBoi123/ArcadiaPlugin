package com.datasiqn.arcadia.util.lorebuilder;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public interface Lore {
    @Unmodifiable
    List<String> asStringList();

    default void addTo(@NotNull List<String> list) {
        list.addAll(asStringList());
    }
    default void addTo(int index, @NotNull List<String> list) {
        list.addAll(index, asStringList());
    }

    @Contract(value = "_ -> new", pure = true)
    static @NotNull Lore of(String @NotNull ... lore) {
        LoreBuilder builder = new LoreBuilder();
        for (String s : lore) {
            builder.append(s);
        }
        return builder.build();
    }
}
