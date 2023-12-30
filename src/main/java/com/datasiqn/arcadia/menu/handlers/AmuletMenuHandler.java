package com.datasiqn.arcadia.menu.handlers;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.item.ArcadiaItem;
import com.datasiqn.arcadia.item.type.ItemType;
import com.datasiqn.arcadia.player.ArcadiaSender;
import com.datasiqn.arcadia.player.PlayerData;
import com.datasiqn.arcadia.util.ItemUtil;
import com.datasiqn.menuapi.inventory.MenuHandler;
import com.datasiqn.menuapi.inventory.item.StaticMenuItem;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class AmuletMenuHandler extends MenuHandler {
    private final Arcadia plugin;

    public AmuletMenuHandler(Arcadia plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onClose(@NotNull InventoryCloseEvent event) {
        super.onClose(event);

        plugin.saveConfig();
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(event.getPlayer().getUniqueId());
        if (playerData == null) return;
        playerData.updateValues();
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        super.onClick(event);

        event.setCancelled(true);

        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;
        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null) return;
        ArcadiaItem arcadiaItem = new ArcadiaItem(itemStack);
        arcadiaItem.setAmount(1);
        if (arcadiaItem.getData().getType() == ItemType.POWER_STONE) {
            PlayerData playerData = plugin.getPlayerManager().getPlayerData(event.getWhoClicked().getUniqueId());
            if (playerData == null) {
                new ArcadiaSender<>(event.getWhoClicked()).sendError("Failed to get player data");
                return;
            }
            ArcadiaItem[] amulet = playerData.getEquipment().getAmulet();
            if (event.getClickedInventory().equals(event.getView().getTopInventory())) {
                Map<Integer, ItemStack> overflow = event.getWhoClicked().getInventory().addItem(arcadiaItem.build());
                if (!overflow.isEmpty()) return;
                event.setCurrentItem(ItemUtil.createEmpty(Material.GRAY_STAINED_GLASS_PANE));
                amulet[event.getSlot()] = null;
            } else {
                int firstEmptyIndex = ArrayUtils.indexOf(amulet, null);
                if (firstEmptyIndex == ArrayUtils.INDEX_NOT_FOUND) return;
                if (itemStack.getAmount() == 1) {
                    event.setCurrentItem(null);
                } else {
                    itemStack.setAmount(itemStack.getAmount() - 1);
                    event.setCurrentItem(itemStack);
                }
                event.getInventory().setItem(firstEmptyIndex, arcadiaItem.build());
                amulet[firstEmptyIndex] = arcadiaItem;
            }
            new Thread(playerData::saveData).start();
        }
    }

    @Override
    public void onDrag(@NotNull InventoryDragEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void populate(@NotNull HumanEntity humanEntity) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(humanEntity.getUniqueId());
        if (playerData == null) {
            new ArcadiaSender<>(humanEntity).sendError("Failed to get player data");
            return;
        }
        @Nullable ArcadiaItem[] amulet = playerData.getEquipment().getAmulet();

        ItemStack empty = ItemUtil.createEmpty(Material.GRAY_STAINED_GLASS_PANE);
        for (int i = 0; i < 9; i++) {
            setItem(i, amulet[i] == null ? new StaticMenuItem(empty) : new StaticMenuItem(amulet[i].build()));
        }
    }

    @Override
    public @NotNull Inventory createInventory() {
        return Bukkit.createInventory(null, InventoryType.DISPENSER, "Amulet");
    }
}
