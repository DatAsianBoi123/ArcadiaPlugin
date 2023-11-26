package com.datasiqn.arcadia.upgrade.listeners;

import com.datasiqn.arcadia.player.PlayerAttribute;
import com.datasiqn.arcadia.upgrade.actions.UpdateAttributesAction;
import org.jetbrains.annotations.NotNull;

public class SneakerListener implements UpgradeListener {
    @ActionHandler(priority = 0)
    public void onUpdateAttributes(@NotNull UpdateAttributesAction action, int stackSize) {
        action.getAttributes().computeDoubleIfPresent(PlayerAttribute.SPEED, (attribute, speed) -> speed + 0.1 * stackSize);
    }
}
