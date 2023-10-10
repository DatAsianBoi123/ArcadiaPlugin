package com.datasiqn.arcadia.managers;

import com.datasiqn.arcadia.Arcadia;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

public class ScoreboardManager {
    public static final String OBJECTIVE_NAME = "main";
    private final Arcadia plugin;

    public ScoreboardManager(Arcadia plugin) {
        this.plugin = plugin;
    }

    public void updateScoreboard(@NotNull Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective(OBJECTIVE_NAME);
        if (objective == null) return;
        objective.unregister();
        objective = registerObjective(scoreboard);
        objective.getScore(ChatColor.GRAY + "Welcome, " + ChatColor.YELLOW + ChatColor.BOLD + player.getName() + ChatColor.RESET + ChatColor.GRAY + "!").setScore(1);
        objective.getScore(String.valueOf(plugin.getPlayerManager().getPlayerData(player).getXp().getLevel())).setScore(0);
    }

    public void createScoreboard(@NotNull Player player) {
        if (!player.isOnline()) return;

        org.bukkit.scoreboard.ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        if (scoreboardManager == null) return;
        if (player.getScoreboard() != scoreboardManager.getMainScoreboard()) return;
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
        registerObjective(scoreboard);
        player.setScoreboard(scoreboard);
    }

    private @NotNull Objective registerObjective(@NotNull Scoreboard scoreboard) {
        Objective objective = scoreboard.registerNewObjective(OBJECTIVE_NAME, Criteria.DUMMY, ChatColor.GREEN + "" + ChatColor.BOLD + "Arcadia");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        return objective;
    }
}
