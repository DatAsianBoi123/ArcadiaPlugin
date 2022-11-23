package com.datasiqn.arcadia.events;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaKeys;
import com.datasiqn.arcadia.datatype.ArcadiaDataType;
import com.datasiqn.arcadia.guis.StaticGUI;
import com.datasiqn.arcadia.items.ArcadiaItem;
import com.datasiqn.arcadia.items.abilities.AbilityExecutor;
import com.datasiqn.arcadia.items.abilities.ItemAbility;
import com.datasiqn.arcadia.players.PlayerData;
import com.datasiqn.arcadia.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
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
    public void onPlayerOpenBag(@NotNull InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if (inventory == null) return;
        InventoryHolder holder = inventory.getHolder();
        if (holder == null) return;
        HumanEntity whoClicked = event.getWhoClicked();
        if (!holder.equals(whoClicked)) return;

        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (!pdc.getOrDefault(ArcadiaKeys.UPGRADE_BAG, ArcadiaDataType.BOOLEAN, false)) return;
        event.setCancelled(true);
        Inventory bagInventory = new StaticGUI(54, "Item Bag") {}.getInventory();
        ItemStack empty = ItemUtil.createEmpty(Material.GRAY_STAINED_GLASS_PANE);
        ItemStack closeItem = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeItem.getItemMeta();
        if (closeMeta == null) return;
        closeMeta.setDisplayName(ChatColor.RED + "Close");
        closeItem.setItemMeta(closeMeta);
        for (int i = 0; i < 9; i++) bagInventory.setItem(53 - i, empty);
        bagInventory.setItem(49, closeItem);
        plugin.runAfterOneTick(() -> {
            whoClicked.getWorld().playSound(whoClicked, Sound.BLOCK_CHEST_OPEN, 1, 1);
            whoClicked.openInventory(bagInventory);
        });
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
