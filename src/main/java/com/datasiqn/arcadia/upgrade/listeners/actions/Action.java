package com.datasiqn.arcadia.upgrade.listeners.actions;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.dungeon.DungeonPlayer;
import com.datasiqn.arcadia.upgrade.ProcGenerator;

import java.util.Random;

public abstract class Action {
    protected final DungeonPlayer playerData;
    private final Arcadia plugin;
    protected final ProcGenerator procGenerator;

    public Action(DungeonPlayer player, Arcadia plugin) {
        this.playerData = player;
        this.plugin = plugin;
        this.procGenerator = new ProcGenerator(playerData, new Random(), plugin);
    }

    public DungeonPlayer getPlayer() {
        return playerData;
    }

    public ProcGenerator getProcGenerator() {
        return procGenerator;
    }

    public Arcadia getPlugin() {
        return plugin;
    }
}
