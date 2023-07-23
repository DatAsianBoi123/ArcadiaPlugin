package com.datasiqn.arcadia.upgrade.listeners;

import com.datasiqn.arcadia.upgrade.listeners.actions.DamageEnemyAction;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class LightningBottleListener implements UpgradeListener {
    @ActionHandler
    public void onHit(DamageEnemyAction action, int stackSize) {
        if (Math.random() < 0.5) {
            Player player = action.getPlayer().getPlayerData().getPlayer();
            player.getWorld().strikeLightningEffect(action.getEntity().getBukkitEntity().getLocation());
            ((LivingEntity) action.getEntity().getBukkitEntity()).damage(action.getDamage() * (5 + stackSize * 2), player);
        }
    }
}
