package com.datasiqn.arcadia.menu.handlers;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.player.ArcadiaSender;
import com.datasiqn.arcadia.player.Experience;
import com.datasiqn.arcadia.player.PlayerData;
import com.datasiqn.arcadia.util.ItemUtil;
import com.datasiqn.menuapi.inventory.MenuHandler;
import com.datasiqn.menuapi.inventory.item.MenuButton;
import com.datasiqn.menuapi.inventory.item.StaticMenuItem;
import com.datasiqn.schedulebuilder.ScheduleBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class LevelMenuHandler extends MenuHandler {
    private static final ItemStack BACK_ARROW;
    static {
        BACK_ARROW = new ItemStack(Material.ARROW);
        ItemMeta meta = BACK_ARROW.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RESET + "Back");
            BACK_ARROW.setItemMeta(meta);
        }
    }

    private static final ItemStack FORWARD_ARROW;
    static {
        FORWARD_ARROW = new ItemStack(Material.ARROW);
        ItemMeta meta = FORWARD_ARROW.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RESET + "Forward");
            FORWARD_ARROW.setItemMeta(meta);
        }
    }

    private final Arcadia plugin;

    private int offset = 0;

    public LevelMenuHandler(Arcadia plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {
        super.onOpen(event);

        PlayerData playerData = plugin.getPlayerManager().getPlayerData(event.getPlayer().getUniqueId());
        if (playerData == null) {
            new ArcadiaSender<>(event.getPlayer()).sendError("Could not load player data. Please try again later");
            return;
        }
        Inventory inventory = event.getInventory();
        generateLines(inventory, inventory.getSize() / 9 - 1, playerData.getXp());
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        super.onClick(event);

        event.setCancelled(true);
    }

    @Override
    public void populate(HumanEntity humanEntity, Inventory inv) {
        ItemStack icon = ItemUtil.createEmpty(Material.GRAY_STAINED_GLASS_PANE);
        for (int i = 0; i < 54; i++) {
            if (i == 49) {
                ItemStack itemStack = new ItemStack(Material.BARRIER);
                ItemMeta meta = itemStack.getItemMeta();
                if (meta == null) continue;
                meta.setDisplayName(ChatColor.RED + "Close");
                itemStack.setItemMeta(meta);
                setItem(i, new MenuButton(itemStack)
                        .onClick(event -> ScheduleBuilder.create().executes(runnable -> humanEntity.closeInventory()).run(plugin)));
                continue;
            } else if (i == 53 || i == 45) {
                boolean isForward = i == 53;
                ItemStack itemStack = isForward ? FORWARD_ARROW : ItemUtil.createEmpty(Material.GRAY_STAINED_GLASS_PANE);
                ItemMeta meta = itemStack.getItemMeta();
                if (meta == null) continue;
                meta.setDisplayName(ChatColor.RESET + (isForward ? "Forward" : "Back"));
                itemStack.setItemMeta(meta);
                setItem(i, new MenuButton(itemStack)
                        .onClick(event -> {
                            PlayerData playerData = plugin.getPlayerManager().getPlayerData(event.getWhoClicked().getUniqueId());
                            if (playerData == null) return;
                            Inventory inventory = event.getInventory();
                            offset += isForward ? 1 : -1;
                            if (offset <= 0) {
                                inventory.setItem(45, ItemUtil.createEmpty(Material.GRAY_STAINED_GLASS_PANE));
                                if (offset < 0) {
                                    offset = 0;
                                    return;
                                }
                            } else {
                                inventory.setItem(45, BACK_ARROW);
                            }
                            int height = inventory.getSize() / 9 - 1;
                            for (int j = 0; j < height; j++) {
                                for (int k = 0; k < 9; k++) {
                                    inventory.setItem(j * 9 + k, icon);
                                }
                            }
                            generateLines(inventory, height, playerData.getXp());
                        }));
                continue;
            }

            setItem(i, new StaticMenuItem(icon));
        }
    }

    @Override
    public @NotNull Inventory createInventory() {
        return Bukkit.createInventory(null, 54, "Level Menu");
    }

    private void generateLines(@NotNull Inventory inventory, int height, Experience exp) {
        for (int offset = this.offset / 2; offset < 4 + this.offset / 2d; offset++) {
            for (int i = 0; i < height; i++) {
                int level = offset * height + offset + 2;
                if (offset % 2 == 0) level += i;
                else level += (height - i - 1);

                int column = offset * 2 + 1 - this.offset;
                if (column < 0) break;
                inventory.setItem(i * 9 + column, createLevelIcon(level, getProgress(level, exp)));
            }
        }

        for (int offset = (int) Math.ceil(this.offset / 2d); offset <= 4 + this.offset / 2; offset++) {
            int level = offset * (height + 1) + 1;
            double progress = getProgress(level, exp);
            int top = offset * 2 - this.offset;
            if (top < 0) continue;
            if (offset % 2 == 0) {
                inventory.setItem(top, createLevelIcon(level, progress));
            } else {
                inventory.setItem(top + 9 * (height - 1), createLevelIcon(level, progress));
            }
        }
    }

    private double getProgress(int level, @NotNull Experience exp) {
        double progress = (exp.getLevel() + exp.getProgress()) - (level - 1);
        if (progress < 0) progress = 0;
        else if (progress > 1) progress = 1;
        return progress;
    }

    private @NotNull ItemStack createLevelIcon(int level, double progress) {
        ItemStack itemStack;
        ChatColor color = ChatColor.GRAY;
        boolean enchanted = false;
        if (progress == 0) itemStack = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        else if (progress == 1) {
            itemStack = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            color = ChatColor.GREEN;
            enchanted = true;
        } else {
            itemStack = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
            color = ChatColor.YELLOW;
        }

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return itemStack;
        if (enchanted) {
            meta.addEnchant(Enchantment.DURABILITY, 0, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        meta.setDisplayName(ChatColor.GRAY + "Level " + level + " " + color + "" + ChatColor.BOLD + Math.round(progress * 100) + "%");
        itemStack.setItemMeta(meta);

        return itemStack;
    }
}
