package com.datasiqn.arcadia.item;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
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
    private final Team team;

    ItemRarity(String displayName, ChatColor color) {
        this.displayName = displayName;
        this.color = color;
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        if (scoreboardManager == null) {
            this.team = null;
            return;
        }
        Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
        Team team = scoreboard.getTeam(name());
        if (team != null) {
            this.team = team;
            return;
        }
        this.team = scoreboard.registerNewTeam(name());
        this.team.setColor(color);
    }

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return color + "" + ChatColor.BOLD + displayName;
    }

    public ChatColor getColor() {
        return color;
    }

    public Team getTeam() {
        return team;
    }
}
