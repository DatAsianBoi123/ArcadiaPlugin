package com.datasiqn.arcadia.upgrade.listeners;

import com.datasiqn.arcadia.player.ArcadiaSender;
import com.datasiqn.arcadia.upgrade.listeners.actions.TryProcAction;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LotteryTicketListener implements UpgradeListener {
    @ActionHandler(priority = 0)
    public void onTryProc(@NotNull TryProcAction action, int stackSize) {
        ArcadiaSender<Player> sender = action.getPlayer().getSender();
        if (action.isProcced()) {
            sender.sendDebugMessage("item already procced, returning");
            return;
        }
        boolean procced = false;
        for (int i = 0; i < stackSize; i++) {
            if (procced) {
                sender.sendDebugMessage("Item has already procced, skipping all remaining re-rolls");
                break;
            }
            procced = action.getProcGenerator().tryProc(action.getChance(), false);
            sender.sendDebugMessage("Rerolled, item has procced? " + procced);
        }
        sender.sendDebugMessage("Finished all re-rolls. Item has procced? " + procced);
        action.setProcced(procced);
    }
}
