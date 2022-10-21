package com.datasiqn.arcadia.guis;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.items.ArcadiaItem;
import com.datasiqn.arcadia.items.type.ItemType;
import com.datasiqn.arcadia.players.PlayerData;
import com.datasiqn.arcadia.util.ItemUtil;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class AmuletGUI extends ArcadiaGUI {
    private final Arcadia plugin;

    public AmuletGUI(Arcadia plugin) {
        super(InventoryType.DISPENSER, "Amulet");
        this.plugin = plugin;

        ItemStack empty = ItemUtil.createEmpty(Material.GRAY_STAINED_GLASS_PANE);
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, empty);
        }
    }

    @Override
    public void openEvent(@NotNull InventoryOpenEvent event) {
        ItemStack empty = ItemUtil.createEmpty(Material.GRAY_STAINED_GLASS_PANE);
        for (int i = 0; i < 9; i++) {
            ArcadiaItem amuletItem = plugin.getPlayerManager().getPlayerData(event.getPlayer().getUniqueId()).getEquipment().getAmulet()[i];
            inv.setItem(i, amuletItem == null ? empty : amuletItem.build());
        }
    }

    @Override
    public void closeEvent(@NotNull InventoryCloseEvent event) {
        plugin.saveConfig();
        plugin.getPlayerManager().getPlayerData(event.getPlayer().getUniqueId()).updateValues();
    }

    @Override
    public void clickEvent(@NotNull InventoryInteractEvent event) {
        event.setCancelled(true);

        if (event instanceof InventoryClickEvent clickEvent) {
            Inventory clickedInventory = clickEvent.getClickedInventory();
            if (clickedInventory == null) return;
            boolean clickedTop = clickedInventory.getHolder() instanceof AmuletGUI;
            ItemStack itemStack = clickEvent.getCurrentItem();
            if (itemStack == null) return;
            ArcadiaItem arcadiaItem = new ArcadiaItem(itemStack);
            arcadiaItem.setAmount(1);
            if (arcadiaItem.getItemData().getItemType() == ItemType.POWER_STONE) {
                PlayerData playerData = plugin.getPlayerManager().getPlayerData(clickEvent.getWhoClicked().getUniqueId());
                ArcadiaItem[] amulet = playerData.getEquipment().getAmulet();
                if (clickedTop) {
                    Map<Integer, ItemStack> overflow = clickEvent.getWhoClicked().getInventory().addItem(arcadiaItem.build());
                    if (!overflow.isEmpty()) return;
                    clickEvent.setCurrentItem(ItemUtil.createEmpty(Material.GRAY_STAINED_GLASS_PANE));
                    amulet[clickEvent.getSlot()] = null;
                } else {
                    int firstEmptyIndex = ArrayUtils.indexOf(amulet, null);
                    if (firstEmptyIndex == ArrayUtils.INDEX_NOT_FOUND) return;
                    if (itemStack.getAmount() == 1) {
                        clickEvent.setCurrentItem(null);
                    } else {
                        itemStack.setAmount(itemStack.getAmount() - 1);
                        clickEvent.setCurrentItem(itemStack);
                    }
                    inv.setItem(firstEmptyIndex, arcadiaItem.build());
                    amulet[firstEmptyIndex] = arcadiaItem;
                }
                playerData.saveData();
            }
        }
    }
}
