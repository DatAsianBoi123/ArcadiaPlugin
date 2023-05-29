package com.datasiqn.arcadia.util.lorebuilder.component;

import org.bukkit.ChatColor;
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
        return text(text, ChatColor.GRAY);
    }
    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull TextLoreComponent text(String text, ChatColor color) {
        return new TextLoreComponent(ChatColor.RESET + "" + color + text);
    }
}
