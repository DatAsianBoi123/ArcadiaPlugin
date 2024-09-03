package com.datasiqn.arcadia.upgrade.listeners;

import com.datasiqn.arcadia.dungeon.DungeonPlayer;
import com.datasiqn.arcadia.upgrade.actions.PlayerDamagedAction;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DefibrillatorListener implements UpgradeListener {
    @ActionHandler(priority = 0)
    public void onTakeLethalDamage(@NotNull PlayerDamagedAction action, int stackSize) {
        DungeonPlayer dungeonPlayer = action.getPlayer();
        double playerHealth = dungeonPlayer.getPlayerData().getHealth();
        if (action.getDamage() >= playerHealth) {
            // TODO: use player effects for cooldown
            Player player = dungeonPlayer.getPlayer();
            player.playSound(player, Sound.ITEM_TOTEM_USE, 0.5f, 2);
            action.setDamage(playerHealth - 10 * stackSize);
        }
    }
}
