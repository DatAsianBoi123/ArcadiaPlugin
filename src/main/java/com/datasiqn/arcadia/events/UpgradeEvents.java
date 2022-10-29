package com.datasiqn.arcadia.events;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaKeys;
import com.datasiqn.arcadia.datatype.ArcadiaDataType;
import com.datasiqn.arcadia.items.ArcadiaItem;
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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class UpgradeEvents implements Listener {
    private final Arcadia plugin;

    public UpgradeEvents(Arcadia plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerOpenChest(@NotNull PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (block == null) return;
        if (block.getType() != Material.ENDER_CHEST) return;

        event.setCancelled(true);

        EnderChest enderChest = (EnderChest) block.getState();
        PersistentDataContainer pdc = enderChest.getPersistentDataContainer();

        if (pdc.getOrDefault(ArcadiaKeys.CHEST_OPENED, ArcadiaDataType.BOOLEAN, false)) {
            plugin.getPlayerManager().getPlayerData(event.getPlayer()).getPlayer().sendError("You have already opened this chest!");
            return;
        }

        pdc.set(ArcadiaKeys.CHEST_OPENED, ArcadiaDataType.BOOLEAN, true);
        enderChest.update();

        Location location = block.getLocation();
        block.getWorld().spawnParticle(Particle.CLOUD, location.clone().add(0.5, 1.1, 0.5), 20, 0.1, 0.1, 0.1, 0.05);
        block.getWorld().spawn(location.clone().add(0.5, 1, 0.5), Item.class, item -> {
            item.setVelocity(new Vector());
            item.setGravity(false);
            item.setUnlimitedLifetime(true);
            item.setGlowing(true);
            item.setPickupDelay(40);
            item.getItemStack().setType(Material.DIAMOND);
            item.setCustomName("Some Cool Upgrade");
            item.setCustomNameVisible(true);

            PersistentDataContainer itemPdc = item.getPersistentDataContainer();
            itemPdc.set(ArcadiaKeys.CHEST_LOC, PersistentDataType.INTEGER_ARRAY, new int[]{location.getBlockX(), location.getBlockY(), location.getBlockZ()});
        });
        enderChest.open();
        event.getPlayer().playSound(location, Sound.BLOCK_ENDER_CHEST_OPEN, 1, 1);
    }

    @EventHandler
    public void onPlayerPickupUpgrade(@NotNull EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        PersistentDataContainer pdc = event.getItem().getPersistentDataContainer();
        if (!pdc.has(ArcadiaKeys.CHEST_LOC, PersistentDataType.INTEGER_ARRAY)) return;

        int[] locationArray = pdc.get(ArcadiaKeys.CHEST_LOC, PersistentDataType.INTEGER_ARRAY);
        if (locationArray == null) return;
        if (locationArray.length < 3) return;

        Location chestLocation = new Location(player.getWorld(), locationArray[0], locationArray[1], locationArray[2]);
        Block block = player.getWorld().getBlockAt(chestLocation);
        if (block.getType() != Material.ENDER_CHEST) return;
        EnderChest enderChest = (EnderChest) block.getState();
        enderChest.close();

        event.getItem().setItemStack(new ArcadiaItem(Material.STICK).build());
        player.playSound(chestLocation, Sound.BLOCK_ENDER_CHEST_CLOSE, 1, 1);
    }
}
