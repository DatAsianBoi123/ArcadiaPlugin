package com.datasiqn.arcadia.listeners;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaTag;
import com.datasiqn.arcadia.dungeon.DungeonInstance;
import com.datasiqn.arcadia.dungeon.DungeonPlayer;
import com.datasiqn.arcadia.player.PlayerData;
import com.datasiqn.arcadia.upgrade.UpgradeType;
import com.datasiqn.arcadia.util.PdcUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Item;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.EnderChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class UpgradeListener implements Listener {
    private final Arcadia plugin;

    public UpgradeListener(Arcadia plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerOpenChest(@NotNull PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || block == null) return;
        if (block.getType() != Material.ENDER_CHEST) return;

        EnderChest enderChest = (EnderChest) block.getState();
        PersistentDataContainer pdc = enderChest.getPersistentDataContainer();

        if (!PdcUtil.getOrDefault(pdc, ArcadiaTag.UPGRADE_CHEST, false)) return;

        event.setCancelled(true);

        PlayerData playerData = plugin.getPlayerManager().getPlayerData(event.getPlayer());

        if (PdcUtil.getOrDefault(pdc, ArcadiaTag.CHEST_OPENED, false)) {
            playerData.getSender().sendError("You have already opened this chest!");
            return;
        }

        PdcUtil.set(pdc, ArcadiaTag.CHEST_OPENED, true);
        enderChest.update();

        Location location = block.getLocation();
        block.getWorld().spawnParticle(Particle.CLOUD, location.clone().add(0.5, 1.1, 0.5), 20, 0.1, 0.1, 0.1, 0.05);
        Location upgradeLocation = location.clone().add(0.5, 1, 0.5);
        DungeonInstance dungeon = plugin.getDungeonManager().getJoinedDungeon(playerData);
        if (dungeon == null) return;
        dungeon.generateUpgrade(upgradeLocation, dungeon.getPlayer(playerData), item -> {
            item.setGravity(false);
            item.setPickupDelay(40);

            PdcUtil.set(item.getPersistentDataContainer(), ArcadiaTag.CHEST_LOC, location);
        });
        enderChest.open();
        event.getPlayer().playSound(location, Sound.ENTITY_ITEM_PICKUP, 0.2f, 1);
        event.getPlayer().playSound(location, Sound.BLOCK_ENDER_CHEST_OPEN, 1, 1);
    }

    @EventHandler
    public void onPlayerPickupUpgrade(@NotNull EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        PersistentDataContainer pdc = event.getItem().getPersistentDataContainer();
        if (!PdcUtil.has(pdc, ArcadiaTag.UPGRADE_TYPE)) return;

        ItemStack itemStack = event.getItem().getItemStack();
        itemStack.setType(Material.AIR);
        event.getItem().setItemStack(itemStack);
        DungeonPlayer dungeonPlayer = plugin.getDungeonManager().getDungeonPlayer(player);
        if (dungeonPlayer == null) return;
        UpgradeType upgradeType = PdcUtil.get(pdc, ArcadiaTag.UPGRADE_TYPE);
        dungeonPlayer.pickupUpgrade(upgradeType);
        ItemStack upgradeItem = upgradeType.getData().toItemStack(1, UUID.randomUUID());
        ItemMeta meta = upgradeItem.getItemMeta();
        if (meta == null) return;
        dungeonPlayer.getPlayer().spigot().sendMessage(new ComponentBuilder()
                .append("You picked up ").color(ChatColor.GRAY)
                .append(TextComponent.fromLegacyText(meta.getDisplayName()))
                .event(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new Item(upgradeItem.getType().getKey().getKey(), 1, ItemTag.ofNbt(meta.getAsString()))))
                .append(" (" + dungeonPlayer.getUpgradeAmount(upgradeType) + ")").reset().color(ChatColor.GRAY)
                .create());
    }

    @EventHandler
    public void onUpgradeMerge(@NotNull ItemMergeEvent event) {
        PersistentDataContainer mainPdc = event.getEntity().getPersistentDataContainer();
        PersistentDataContainer targetPdc = event.getTarget().getPersistentDataContainer();
        if (PdcUtil.has(mainPdc, ArcadiaTag.UPGRADE_TYPE) || PdcUtil.has(targetPdc, ArcadiaTag.UPGRADE_TYPE)) event.setCancelled(true);
    }
}
