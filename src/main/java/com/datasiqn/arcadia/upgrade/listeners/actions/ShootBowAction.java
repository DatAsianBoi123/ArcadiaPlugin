package com.datasiqn.arcadia.upgrade.listeners.actions;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.dungeon.DungeonPlayer;
import org.bukkit.entity.Arrow;

public class ShootBowAction extends Action {
    private final Arrow arrow;

    public ShootBowAction(DungeonPlayer player, Arrow arrow, Arcadia plugin) {
        super(player, plugin);
        this.arrow = arrow;
    }

    public Arrow getArrow() {
        return arrow;
    }
}
