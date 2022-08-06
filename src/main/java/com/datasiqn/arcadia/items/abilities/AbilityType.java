package com.datasiqn.arcadia.items.abilities;

import org.bukkit.ChatColor;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@SuppressWarnings("unused")
public enum AbilityType {
    RIGHT_CLICK(ChatColor.YELLOW + "" + ChatColor.BOLD + "RIGHT CLICK", new Action[] {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK}),
    LEFT_CLICK(ChatColor.YELLOW + "" + ChatColor.BOLD + "LEFT CLICK", new Action[] {Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK});

    private final String displayName;
    private final Action[] desiredActions;

    AbilityType(String displayName, Action[] desiredActions) {
        this.displayName = displayName;
        this.desiredActions = desiredActions;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public boolean includesActions(@NotNull PlayerInteractEvent event) {
        return Arrays.stream(desiredActions).anyMatch(action -> action.equals(event.getAction()));
    }
}
