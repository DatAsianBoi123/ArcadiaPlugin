package com.datasiqn.arcadia.upgrade.actions;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.damage.DamageCause;
import com.datasiqn.arcadia.entities.ArcadiaEntity;
import org.jetbrains.annotations.NotNull;

public class DamageEnemyAction extends EntityAction {
    private final DamageCause damageCause;
    private double damage;

    public DamageEnemyAction(@NotNull DamageCause damageCause, ArcadiaEntity entity, double damage, Arcadia plugin) {
        super(damageCause.getSource(), entity, plugin);
        this.damageCause = damageCause;
        this.damage = damage;
    }

    public double getDamage() {
        return damage;
    }

    public DamageCause getCause() {
        return damageCause;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }
}
