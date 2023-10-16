package com.datasiqn.arcadia.upgrade.listeners;

import com.datasiqn.arcadia.upgrade.listeners.actions.KillEnemyAction;
import org.jetbrains.annotations.NotNull;

public class BloodChaliceListener implements UpgradeListener {
    @ActionHandler(priority = 10)
    public void onKill(@NotNull KillEnemyAction action, int stackAmount) {
        action.getPlayer().getPlayerData().heal(10 + 5 * stackAmount);
    }
}
