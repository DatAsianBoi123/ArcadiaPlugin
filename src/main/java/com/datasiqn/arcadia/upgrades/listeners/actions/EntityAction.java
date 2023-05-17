package com.datasiqn.arcadia.upgrades.listeners.actions;

import com.datasiqn.arcadia.dungeons.DungeonPlayer;
import com.datasiqn.arcadia.entities.ArcadiaEntity;

public abstract class EntityAction extends Action {
    protected final ArcadiaEntity entity;

    public EntityAction(DungeonPlayer player, ArcadiaEntity entity) {
        super(player);
        this.entity = entity;
    }

    public ArcadiaEntity getEntity() {
        return entity;
    }
}
