package com.datasiqn.arcadia.upgrade.listeners;

import com.datasiqn.arcadia.effect.ArcadiaEffectType;
import com.datasiqn.arcadia.upgrade.actions.DamageEnemyAction;
import com.datasiqn.commandcore.argument.duration.Duration;
import com.datasiqn.commandcore.argument.duration.TimeUnit;
import org.jetbrains.annotations.NotNull;

public class FlamethrowerListener implements UpgradeListener {
    @ActionHandler(priority = 0)
    public void onDamage(@NotNull DamageEnemyAction action, int stackSize) {
        if (!action.getProcGenerator().tryProc(0.2)) return;
        action.getEntity().addArcadiaEffect(ArcadiaEffectType.BURNING, Duration.from(40 + 20L * stackSize, TimeUnit.TICKS), action.getPlayer().getPlayerData());
    }
}
