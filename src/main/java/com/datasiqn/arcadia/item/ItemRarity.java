package com.datasiqn.arcadia.item;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ItemRarity {
    COMMON("COMMON", ChatColor.WHITE),
    RARE("RARE", ChatColor.BLUE),
    LEGENDARY("LEGENDARY", ChatColor.GOLD),
    MYTHIC("MYTHIC", ChatColor.LIGHT_PURPLE),
    SPECIAL("SPECIAL", ChatColor.RED),
    ;

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

    public @Nullable Team getTeam(@NotNull Scoreboard scoreboard) {
        return scoreboard.getTeam(name());
    }

    public static void createTeams(@NotNull Scoreboard scoreboard) {
        for (ItemRarity rarity : values()) {
            if (scoreboard.getTeam(rarity.name()) != null) return;
            Team team = scoreboard.registerNewTeam(rarity.name());
            team.setColor(rarity.color);
        }
    }
}
