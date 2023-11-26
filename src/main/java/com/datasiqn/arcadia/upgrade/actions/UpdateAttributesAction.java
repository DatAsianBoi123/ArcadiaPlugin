package com.datasiqn.arcadia.upgrade.actions;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.dungeon.DungeonPlayer;
import com.datasiqn.arcadia.player.PlayerAttribute;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;

public class UpdateAttributesAction extends Action {
    private final Object2DoubleMap<PlayerAttribute> attributes;

    public UpdateAttributesAction(DungeonPlayer player, Object2DoubleMap<PlayerAttribute> attributes, Arcadia plugin) {
        super(player, plugin);
        this.attributes = attributes;
    }

    public Object2DoubleMap<PlayerAttribute> getAttributes() {
        return attributes;
    }
}
