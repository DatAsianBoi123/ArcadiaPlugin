package com.datasiqn.arcadia.upgrade.listeners;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.upgrade.listeners.actions.ShootBowAction;
import com.datasiqn.schedulebuilder.ScheduleBuilder;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.persistence.CraftPersistentDataContainer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class MagicQuiverListener implements UpgradeListener {
    private final Arcadia plugin;

    public MagicQuiverListener(Arcadia plugin) {
        this.plugin = plugin;
    }

    @ActionHandler
    public void onShootBow(@NotNull ShootBowAction action, int stackSize) {
        Arrow arrow = action.getArrow();
        Vector velocity = arrow.getVelocity();
        Location spawnLocation = arrow.getLocation();
        Player player = action.getPlayer().getPlayerData().getPlayer();

        ScheduleBuilder.create()
                .executes(runnable -> {
                    player.getWorld().spawn(spawnLocation, Arrow.class, newArrow -> {
                        newArrow.setVelocity(velocity);
                        newArrow.setCritical(arrow.isCritical());
                        newArrow.setShooter(arrow.getShooter());
                        CompoundTag arrowTags = ((CraftPersistentDataContainer) arrow.getPersistentDataContainer()).toTagCompound();
                        ((CraftPersistentDataContainer) newArrow.getPersistentDataContainer()).putAll(arrowTags);
                    });
                })
                .wait(1d).ticks()
                .repeat(stackSize).every(1d).ticks()
                .run(plugin);
    }
}
