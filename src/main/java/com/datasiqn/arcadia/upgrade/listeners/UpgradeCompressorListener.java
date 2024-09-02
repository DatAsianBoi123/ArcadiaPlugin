package com.datasiqn.arcadia.upgrade.listeners;

import com.datasiqn.arcadia.dungeon.DungeonPlayer;
import com.datasiqn.arcadia.player.ArcadiaSender;
import com.datasiqn.arcadia.upgrade.Upgrade;
import com.datasiqn.arcadia.upgrade.UpgradeType;
import com.datasiqn.arcadia.upgrade.actions.DamageEnemyAction;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class UpgradeCompressorListener implements UpgradeListener {
    @ActionHandler(priority = 0)
    public void onDamage(@NotNull DamageEnemyAction action, int stackSize) {
        DungeonPlayer dungeonPlayer = action.getPlayer();
        int totalUpgrades = dungeonPlayer.getUpgrades().stream()
                .filter(upgrade -> upgrade.getType() != UpgradeType.UPGRADE_COMPRESSOR)
                .mapToInt(Upgrade::getAmount)
                .sum();
        double damageMultiplier = 0.05 * stackSize * totalUpgrades + 1;

        if (dungeonPlayer.getPlayerData().inDebugMode()) {
            DecimalFormat format = new DecimalFormat("#,###.##");
            ArcadiaSender<Player> sender = dungeonPlayer.getSender();
            sender.sendDebugMessage("damage before: " + format.format(action.getDamage()));
            sender.sendDebugMessage("# of upgrades: " + totalUpgrades);
            sender.sendDebugMessage("damage multiplier: " + format.format(damageMultiplier));
            sender.sendDebugMessage("new damage: " + format.format(damageMultiplier * action.getDamage()));
        }

        action.setDamage(damageMultiplier * action.getDamage());
    }
}
