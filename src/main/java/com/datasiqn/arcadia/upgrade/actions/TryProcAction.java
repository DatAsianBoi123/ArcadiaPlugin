package com.datasiqn.arcadia.upgrade.actions;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.dungeon.DungeonPlayer;

public class TryProcAction extends Action {
    private boolean procced;
    private final double generatedNumber;
    private final double chance;

    public TryProcAction(DungeonPlayer player, Arcadia plugin, boolean procced, double generatedNumber, double chance) {
        super(player, plugin);
        this.procced = procced;
        this.generatedNumber = generatedNumber;
        this.chance = chance;
    }

    public boolean isProcced() {
        return procced;
    }

    public void setProcced(boolean procced) {
        this.procced = procced;
    }

    public double getGeneratedNumber() {
        return generatedNumber;
    }

    public double getChance() {
        return chance;
    }
}
