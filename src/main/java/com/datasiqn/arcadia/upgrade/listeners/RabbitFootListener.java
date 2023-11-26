package com.datasiqn.arcadia.upgrade.listeners;

import com.datasiqn.arcadia.upgrade.UpgradeType;
import com.datasiqn.arcadia.upgrade.actions.GenerateUpgradeAction;
import org.jetbrains.annotations.NotNull;

public class RabbitFootListener implements UpgradeListener {
    @ActionHandler(priority = 0)
    public void onGenerateUpgrade(@NotNull GenerateUpgradeAction action, int stackSize) {
        UpgradeType currentUpgrade = action.getGenerated();
        for (int i = 0; i < stackSize; i++) {
            UpgradeType newGenerated = UpgradeType.getRandomWeighted();
            if (newGenerated.getData().getRarity().compareTo(currentUpgrade.getData().getRarity()) > 0) {
                currentUpgrade = newGenerated;
            }
        }
        action.setGenerated(currentUpgrade);
    }
}
