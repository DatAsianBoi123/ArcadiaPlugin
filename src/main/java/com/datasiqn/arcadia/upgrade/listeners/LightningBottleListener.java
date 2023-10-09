package com.datasiqn.arcadia.upgrade.listeners;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.entities.ArcadiaEntity;
import com.datasiqn.arcadia.upgrade.listeners.actions.DamageEnemyAction;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LightningBottleListener implements UpgradeListener {
    private final Arcadia plugin;

    public LightningBottleListener(Arcadia plugin) {
        this.plugin = plugin;
    }

    @ActionHandler
    public void onHit(@NotNull DamageEnemyAction action, int stackSize) {
        if (Math.random() < 0.5) {
            Player player = action.getPlayer().getPlayer();
            ArcadiaEntity arcadiaEntity = action.getEntity();

            Bukkit.getScheduler().runTaskLater(plugin, task -> {
                if (arcadiaEntity.isDeadOrDying()) return;
                player.getWorld().strikeLightningEffect(arcadiaEntity.getBukkitEntity().getLocation());
                ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
                arcadiaEntity.damage(action.getDamage() * (5 + stackSize * 2), arcadiaEntity.damageSources().playerAttack(nmsPlayer), action.getPlayer());
            }, 10);
        }
    }
}
