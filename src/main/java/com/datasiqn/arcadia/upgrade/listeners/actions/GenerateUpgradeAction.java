package com.datasiqn.arcadia.upgrade.listeners.actions;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.dungeon.DungeonPlayer;
import com.datasiqn.arcadia.upgrade.UpgradeType;

public class GenerateUpgradeAction extends Action {
    private UpgradeType generated;

    public GenerateUpgradeAction(DungeonPlayer player, UpgradeType generated, Arcadia plugin) {
        super(player, plugin);
        this.generated = generated;
    }

    public UpgradeType getGenerated() {
        return generated;
    }

    public void setGenerated(UpgradeType generated) {
        this.generated = generated;
    }
}
