package com.datasiqn.arcadia.upgrade.actions;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.dungeon.DungeonPlayer;
import com.datasiqn.arcadia.entities.ArcadiaEntity;

public abstract class EntityAction extends Action {
    protected final ArcadiaEntity entity;

    public EntityAction(DungeonPlayer player, ArcadiaEntity entity, Arcadia plugin) {
        super(player, plugin);
        this.entity = entity;
    }

    public ArcadiaEntity getEntity() {
        return entity;
    }
}
