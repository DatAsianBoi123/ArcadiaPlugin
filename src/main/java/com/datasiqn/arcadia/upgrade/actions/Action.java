package com.datasiqn.arcadia.upgrade.actions;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.dungeon.DungeonPlayer;
import com.datasiqn.arcadia.upgrade.ProcGenerator;

import java.util.concurrent.ThreadLocalRandom;

public abstract class Action {
    protected final DungeonPlayer player;
    private final Arcadia plugin;
    protected final ProcGenerator procGenerator;

    public Action(DungeonPlayer player, Arcadia plugin) {
        this.player = player;
        this.plugin = plugin;
        this.procGenerator = new ProcGenerator(this.player, ThreadLocalRandom.current(), plugin);
    }

    public DungeonPlayer getPlayer() {
        return player;
    }

    public ProcGenerator getProcGenerator() {
        return procGenerator;
    }

    public Arcadia getPlugin() {
        return plugin;
    }
}
