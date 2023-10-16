package com.datasiqn.arcadia.upgrade.listeners.actions;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.dungeon.DungeonPlayer;
import com.datasiqn.arcadia.entities.ArcadiaEntity;

public class KillEnemyAction extends EntityAction {
    public KillEnemyAction(DungeonPlayer player, ArcadiaEntity entity, Arcadia plugin) {
        super(player, entity, plugin);
    }
}
