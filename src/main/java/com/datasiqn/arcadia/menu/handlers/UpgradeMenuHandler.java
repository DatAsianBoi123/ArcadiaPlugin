package com.datasiqn.arcadia.menu.handlers;

import com.datasiqn.arcadia.upgrade.Upgrade;
import com.datasiqn.arcadia.upgrade.UpgradeType;
import com.datasiqn.arcadia.util.ItemUtil;
import com.datasiqn.menuapi.inventory.MenuHandler;
import com.datasiqn.menuapi.inventory.item.StaticMenuItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class UpgradeMenuHandler extends MenuHandler {
    private final UpgradeType upgradeType;

    public UpgradeMenuHandler(@NotNull UpgradeType upgradeType) {
        this.upgradeType = upgradeType;
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        super.onClick(event);

        event.setCancelled(true);
    }

    @Override
    public void populate(HumanEntity humanEntity) {
        ItemStack empty = ItemUtil.createEmpty(Material.GRAY_STAINED_GLASS_PANE);
        for (int i = 0; i < 9; i++) {
            if (i == 4) setItem(i, new StaticMenuItem(new Upgrade(upgradeType).toItemStack()));
            else setItem(i, new StaticMenuItem(empty));
        }
    }

    @Override
    public @NotNull Inventory createInventory() {
        return Bukkit.createInventory(null, InventoryType.DISPENSER, upgradeType.getData().getName());
    }
}
