package com.datasiqn.arcadia.items;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum ItemRarity {
    COMMON("COMMON", ChatColor.WHITE),
    RARE("RARE", ChatColor.BLUE),
    LEGENDARY("LEGENDARY", ChatColor.GOLD),
    MYTHIC("MYTHIC", ChatColor.LIGHT_PURPLE),
    SPECIAL("SPECIAL", ChatColor.RED);

    private final String displayName;
    private final ChatColor color;

    ItemRarity(String displayName, ChatColor color) {
        this.displayName = displayName;
        this.color = color;
    }

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return color + "" + ChatColor.BOLD + displayName;
    }

    public ChatColor getColor() {
        return color;
    }
}
