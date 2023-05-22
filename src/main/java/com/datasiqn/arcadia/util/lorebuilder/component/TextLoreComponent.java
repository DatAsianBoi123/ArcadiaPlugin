package com.datasiqn.arcadia.util.lorebuilder.component;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class TextLoreComponent implements LoreComponent {
    private final String text;

    private TextLoreComponent(String text) {
        this.text = text;
    }

    @Override
    public @NotNull String toString() {
        return text;
    }

    @Contract("_ -> new")
    public static @NotNull TextLoreComponent text(String text) {
        return new TextLoreComponent(text);
    }
}
