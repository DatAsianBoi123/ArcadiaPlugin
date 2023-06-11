package com.datasiqn.arcadia.upgrade.listeners.actions;

import com.datasiqn.arcadia.dungeon.DungeonPlayer;

public abstract class Action {
    protected final DungeonPlayer playerData;

    public Action(DungeonPlayer player) {
        this.playerData = player;
    }

    public DungeonPlayer getPlayer() {
        return playerData;
    }
}
