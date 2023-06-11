package com.datasiqn.arcadia.upgrade.listeners.actions;

import com.datasiqn.arcadia.dungeon.DungeonPlayer;
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
