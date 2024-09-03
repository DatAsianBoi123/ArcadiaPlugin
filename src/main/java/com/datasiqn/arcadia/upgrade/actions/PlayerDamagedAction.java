package com.datasiqn.arcadia.upgrade.actions;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.dungeon.DungeonPlayer;
import com.datasiqn.arcadia.entities.ArcadiaEntity;
import jline.internal.Nullable;

public class PlayerDamagedAction extends Action {
    private final ArcadiaEntity damager;
    private double damage;

    public PlayerDamagedAction(DungeonPlayer player, @Nullable ArcadiaEntity damager, double damage, Arcadia plugin) {
        super(player, plugin);
        this.damager = damager;
        this.damage = damage;
    }

    public @Nullable ArcadiaEntity getDamager() {
        return damager;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }
}
