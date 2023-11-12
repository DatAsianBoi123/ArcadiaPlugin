package com.datasiqn.arcadia.upgrade.listeners;

import com.datasiqn.arcadia.entities.ArcadiaHostileEntity;
import com.datasiqn.arcadia.upgrade.listeners.actions.KillEnemyAction;
import org.jetbrains.annotations.NotNull;

public class BloodChaliceListener implements UpgradeListener {
    @ActionHandler(priority = 10)
    public void onKill(@NotNull KillEnemyAction action, int stackAmount) {
        if (!(action.getEntity() instanceof ArcadiaHostileEntity)) return;
        action.getPlayer().getPlayerData().heal(10 + 5 * stackAmount);
    }
}
