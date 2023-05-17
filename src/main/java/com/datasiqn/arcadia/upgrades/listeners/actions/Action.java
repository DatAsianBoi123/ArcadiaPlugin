package com.datasiqn.arcadia.upgrades.listeners.actions;

import com.datasiqn.arcadia.dungeons.DungeonPlayer;

public abstract class Action {
    protected final DungeonPlayer playerData;

    public Action(DungeonPlayer player) {
        this.playerData = player;
    }

    public DungeonPlayer getPlayer() {
        return playerData;
    }
}
