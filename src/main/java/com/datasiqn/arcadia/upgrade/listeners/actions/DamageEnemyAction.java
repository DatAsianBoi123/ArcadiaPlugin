package com.datasiqn.arcadia.upgrade.listeners.actions;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.dungeon.DungeonPlayer;
import com.datasiqn.arcadia.entities.ArcadiaEntity;

public class DamageEnemyAction extends EntityAction {
    private double damage;

    public DamageEnemyAction(DungeonPlayer player, ArcadiaEntity entity, double damage, Arcadia plugin) {
        super(player, entity, plugin);
        this.damage = damage;
    }

    public ArcadiaEntity getEntity() {
        return entity;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }
}
