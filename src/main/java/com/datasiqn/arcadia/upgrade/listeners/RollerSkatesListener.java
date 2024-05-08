package com.datasiqn.arcadia.upgrade.listeners;

import com.datasiqn.arcadia.upgrade.actions.DamageEnemyAction;
import org.jetbrains.annotations.NotNull;

public class RollerSkatesListener implements UpgradeListener {
    @ActionHandler(priority = 10)
    public void onDamage(@NotNull DamageEnemyAction action, int stackSize) {
        if (!action.getPlayer().getPlayer().isSprinting()) return;

        action.setDamage(action.getDamage() * 1.5 * stackSize);
    }
}
