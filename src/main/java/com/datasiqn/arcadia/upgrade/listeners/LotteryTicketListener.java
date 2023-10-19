package com.datasiqn.arcadia.upgrade.listeners;

import com.datasiqn.arcadia.upgrade.listeners.actions.TryProcAction;
import org.jetbrains.annotations.NotNull;

public class LotteryTicketListener implements UpgradeListener {
    @ActionHandler(priority = 0)
    public void onTryProc(@NotNull TryProcAction action, int stackSize) {
        if (action.isProcced()) return;
        boolean procced = false;
        for (int i = 0; i < stackSize; i++) {
            if (procced) break;
            procced = action.getProcGenerator().tryProc(action.getChance(), false);
        }
        action.setProcced(procced);
    }
}
