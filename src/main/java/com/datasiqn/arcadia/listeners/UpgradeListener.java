package com.datasiqn.arcadia.listeners;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaTag;
import com.datasiqn.arcadia.dungeon.DungeonPlayer;
import com.datasiqn.arcadia.player.PlayerData;
import com.datasiqn.arcadia.upgrade.UpgradeData;
import com.datasiqn.arcadia.upgrade.UpgradeType;
import com.datasiqn.arcadia.upgrade.listeners.actions.GenerateUpgradeAction;
import com.datasiqn.arcadia.util.PdcUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.EnderChest;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class UpgradeListener implements Listener {
    private final Arcadia plugin;

    public UpgradeListener(Arcadia plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerOpenChest(@NotNull PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (block == null) return;
        if (block.getType() != Material.ENDER_CHEST) return;

        event.setCancelled(true);

        PlayerData playerData = plugin.getPlayerManager().getPlayerData(event.getPlayer());
        EnderChest enderChest = (EnderChest) block.getState();
        PersistentDataContainer pdc = enderChest.getPersistentDataContainer();

        if (PdcUtil.getOrDefault(pdc, ArcadiaTag.CHEST_OPENED, false)) {
            playerData.getSender().sendError("You have already opened this chest!");
            return;
        }

        PdcUtil.set(pdc, ArcadiaTag.CHEST_OPENED, true);
        enderChest.update();

        Location location = block.getLocation();
        block.getWorld().spawnParticle(Particle.CLOUD, location.clone().add(0.5, 1.1, 0.5), 20, 0.1, 0.1, 0.1, 0.05);
        block.getWorld().spawn(location.clone().add(0.5, 1, 0.5), Item.class, item -> {
            UpgradeType upgradeType = UpgradeType.getRandomWeighted();
            DungeonPlayer dungeonPlayer = plugin.getDungeonManager().getDungeonPlayer(playerData.getUniqueId());
            if (dungeonPlayer != null) {
                GenerateUpgradeAction action = new GenerateUpgradeAction(dungeonPlayer, upgradeType, plugin);
                plugin.getUpgradeEventManager().emit(action);
                upgradeType = action.getGenerated();
            }
            UpgradeData data = upgradeType.getData();
            ItemStack upgrade = data.toItemStack();

            item.setVelocity(new Vector());
            item.setGravity(false);
            item.setUnlimitedLifetime(true);
            item.setGlowing(true);
            item.setPickupDelay(40);
            item.setItemStack(upgrade);
            ItemMeta meta = upgrade.getItemMeta();
            if (meta != null) item.setCustomName(meta.getDisplayName());
            item.setCustomNameVisible(true);

            data.getRarity().getTeam().addEntry(item.getUniqueId().toString());

            PersistentDataContainer itemPdc = item.getPersistentDataContainer();
            PdcUtil.set(itemPdc, ArcadiaTag.CHEST_LOC, location);
            PdcUtil.set(itemPdc, ArcadiaTag.UPGRADE_TYPE, upgradeType);
        });
        enderChest.open();
        event.getPlayer().playSound(location, Sound.ENTITY_ITEM_PICKUP, 0.2f, 1);
        event.getPlayer().playSound(location, Sound.BLOCK_ENDER_CHEST_OPEN, 1, 1);
    }

    @EventHandler
    public void onPlayerPickupUpgrade(@NotNull EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        PersistentDataContainer pdc = event.getItem().getPersistentDataContainer();
        if (!PdcUtil.has(pdc, ArcadiaTag.CHEST_LOC) || !PdcUtil.has(pdc, ArcadiaTag.UPGRADE_TYPE)) return;

        Location chestLocation = PdcUtil.get(pdc, ArcadiaTag.CHEST_LOC);
        Block block = player.getWorld().getBlockAt(chestLocation);
        if (block.getType() != Material.ENDER_CHEST) return;
        EnderChest enderChest = (EnderChest) block.getState();
        enderChest.close();

        ItemStack itemStack = event.getItem().getItemStack();
        itemStack.setType(Material.AIR);
        event.getItem().setItemStack(itemStack);
        DungeonPlayer dungeonPlayer = plugin.getDungeonManager().getDungeonPlayer(plugin.getPlayerManager().getPlayerData(player).getUniqueId());
        if (dungeonPlayer == null) return;
        dungeonPlayer.pickupUpgrade(PdcUtil.get(pdc, ArcadiaTag.UPGRADE_TYPE));
        player.playSound(chestLocation, Sound.BLOCK_ENDER_CHEST_CLOSE, 1, 1);
    }
}
