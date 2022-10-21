package com.datasiqn.arcadia.events;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.items.ArcadiaItem;
import com.datasiqn.arcadia.items.abilities.AbilityExecutor;
import com.datasiqn.arcadia.items.abilities.ItemAbility;
import com.datasiqn.arcadia.players.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemEvents implements Listener {
    private static final Map<UUID, Map<String, Long>> COOLDOWNS = new HashMap<>();
    private final Arcadia plugin;

    public ItemEvents(Arcadia plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerPlaceBlock(@NotNull BlockPlaceEvent event) {
        ItemStack itemInMainHand = event.getPlayer().getInventory().getItemInMainHand();
        ArcadiaItem arcadiaItem = new ArcadiaItem(itemInMainHand);
        if (arcadiaItem.isDefaultMaterial()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) return;
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        if (event.getItem() == null) return;

        ArcadiaItem arcadiaItem = new ArcadiaItem(event.getItem());
        if (arcadiaItem.getMaterial() == null) return;

        ItemAbility itemAbility = arcadiaItem.getItemData().getItemAbility();
        if (itemAbility == null) return;
        DefaultExecutor executor = new DefaultExecutor(plugin.getPlayerManager().getPlayerData(event.getPlayer()), itemAbility);
        if (itemAbility.getType().includesActions(event)) {
            UUID id = event.getPlayer().getUniqueId();
            if (!COOLDOWNS.containsKey(id)) COOLDOWNS.put(id, new HashMap<>());
            long currentTime = System.currentTimeMillis();
            Map<String, Long> playerCooldowns = COOLDOWNS.get(id);
            Long lastUsed = playerCooldowns.getOrDefault(arcadiaItem.getItemData().getID(), -1L);
            if (lastUsed + itemAbility.getCooldown() * 50L > currentTime && lastUsed != -1L) {
                DecimalFormat decimalFormat = new DecimalFormat("#.#");
                executor.playerData().getPlayer().sendMessageRaw(ChatColor.RED + "This ability is on cooldown for " + decimalFormat.format((itemAbility.getCooldown() * 50 - (currentTime - lastUsed)) / 1000d) + "s");
                return;
            }
            COOLDOWNS.get(id).put(arcadiaItem.getItemData().getID(), currentTime);
            itemAbility.execute(executor);
            event.setCancelled(true);
        }
    }

    private record DefaultExecutor(PlayerData playerData, ItemAbility ability) implements AbilityExecutor {
    }
}
