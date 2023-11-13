package com.datasiqn.arcadia.upgrade.listeners;

import com.datasiqn.arcadia.dungeon.DungeonPlayer;
import com.datasiqn.arcadia.entities.ArcadiaHostileEntity;
import com.datasiqn.arcadia.upgrade.listeners.actions.KillEnemyAction;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class UpgradeSynthesizerListener implements UpgradeListener {
    @ActionHandler(priority = 0)
    public void onKill(@NotNull KillEnemyAction action, int stackSize) {
        if (!(action.getEntity() instanceof ArcadiaHostileEntity)) return;
        double chance = -Math.pow(0.97, stackSize) + 1;
        DungeonPlayer player = action.getPlayer();
        if (action.getProcGenerator().tryProc(chance)) {
            Vec3 entityPosition = action.getEntity().position();
            player.getDungeon().generateUpgrade(new Location(null, entityPosition.x, entityPosition.y, entityPosition.z), player);
        }
    }
}
