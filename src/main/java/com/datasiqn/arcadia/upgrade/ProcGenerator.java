package com.datasiqn.arcadia.upgrade;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.dungeon.DungeonPlayer;
import com.datasiqn.arcadia.upgrade.listeners.actions.TryProcAction;

import java.util.random.RandomGenerator;

public class ProcGenerator {
    private final DungeonPlayer player;
    private final RandomGenerator randomGenerator;
    private final Arcadia plugin;

    public ProcGenerator(DungeonPlayer player, RandomGenerator randomGenerator, Arcadia plugin) {
        this.player = player;
        this.randomGenerator = randomGenerator;
        this.plugin = plugin;
    }

    public boolean tryProc(double successChance) {
        return tryProc(successChance, true);
    }
    public boolean tryProc(double successChance, boolean emitAction) {
        double generated = randomGenerator.nextDouble();
        boolean procced = generated < successChance;
        if (!emitAction) return procced;
        TryProcAction action = new TryProcAction(player, plugin, procced, generated, successChance);
        plugin.getUpgradeEventManager().emit(action);
        return action.isProcced();
    }
}
