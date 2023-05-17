package com.datasiqn.arcadia.upgrades.listeners.actions;

import com.datasiqn.arcadia.dungeons.DungeonPlayer;
import com.datasiqn.arcadia.entities.ArcadiaEntity;

public class DamageEnemyAction extends EntityAction {
    private final double damage;

    public DamageEnemyAction(DungeonPlayer player, ArcadiaEntity entity, double damage) {
        super(player, entity);
        this.damage = damage;
    }

    public ArcadiaEntity getEntity() {
        return entity;
    }

    public double getDamage() {
        return damage;
    }
}
