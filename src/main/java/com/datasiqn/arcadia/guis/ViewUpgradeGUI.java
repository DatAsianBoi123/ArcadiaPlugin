package com.datasiqn.arcadia.guis;

import com.datasiqn.arcadia.upgrades.Upgrade;
import com.datasiqn.arcadia.upgrades.UpgradeType;
import com.datasiqn.arcadia.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ViewUpgradeGUI extends StaticGUI {
    public ViewUpgradeGUI(@NotNull UpgradeType upgradeType) {
        super(InventoryType.DISPENSER, upgradeType.getDisplayName());

        ItemStack empty = ItemUtil.createEmpty(Material.GRAY_STAINED_GLASS_PANE);
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, empty);
        }

        inv.setItem(4, new Upgrade(upgradeType).toItemStack());
    }
}
