package com.datasiqn.arcadia.upgrade.listeners;

import com.datasiqn.arcadia.damage.DamageCause;
import com.datasiqn.arcadia.entities.ArcadiaEntity;
import com.datasiqn.arcadia.upgrade.actions.DamageEnemyAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LightningBottleListener implements UpgradeListener {
    @ActionHandler(priority = 99)
    public void onDamage(@NotNull DamageEnemyAction action, int stackSize) {
        if (!action.getCause().isDirect()) return;
        if (action.getProcGenerator().tryProc(0.1)) {
            Player player = action.getPlayer().getPlayer();
            ArcadiaEntity arcadiaEntity = action.getEntity();

            Bukkit.getScheduler().runTaskLater(action.getPlugin(), task -> {
                if (arcadiaEntity.isDeadOrDying()) return;
                player.getWorld().strikeLightningEffect(arcadiaEntity.getBukkitEntity().getLocation());
                arcadiaEntity.damage(action.getDamage() * (5 + stackSize * 2), DamageCause.direct(action.getPlayer()), true);
            }, 10);
        }
    }
}
