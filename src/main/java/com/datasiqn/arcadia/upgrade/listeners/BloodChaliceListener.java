package com.datasiqn.arcadia.upgrade.listeners;

import com.datasiqn.arcadia.upgrade.listeners.actions.DamageEnemyAction;
import com.datasiqn.arcadia.upgrade.listeners.actions.KillEnemyAction;
import org.jetbrains.annotations.NotNull;

public class BloodChaliceListener implements UpgradeListener {
    @ActionHandler
    public void onPlayerDamageEnemy(@NotNull DamageEnemyAction action, int stackAmount) {
        action.getPlayer().getSender().sendMessage("You attacked something " + stackAmount);
    }

    @ActionHandler
    public void onPlayerKillEnemy(@NotNull KillEnemyAction action, int stackAmount) {
        action.getPlayer().getSender().sendMessage("you were healed " + (10 + 5 * stackAmount));
        action.getPlayer().getPlayerData().heal(10 + 5 * stackAmount);
    }
}
