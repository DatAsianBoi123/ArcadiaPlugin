package com.datasiqn.arcadia.listeners;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.items.ArcadiaItem;
import com.datasiqn.arcadia.items.type.ItemType;
import com.datasiqn.arcadia.items.type.data.ConsumableData;
import com.datasiqn.arcadia.players.PlayerData;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ConsumableEvents implements Listener {
    private final Arcadia plugin;

    public ConsumableEvents(Arcadia plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerConsumeItem(@NotNull PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) return;
        ItemStack item = event.getItem();
        if (item == null) return;
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(event.getPlayer());
        ArcadiaItem arcadiaItem = playerData.getEquipment().getItem(event.getHand());
        if (arcadiaItem.getItemData().getItemType() != ItemType.CONSUMABLE) return;
        if (event.getHand() != ItemType.CONSUMABLE.getSlot()) return;
        ConsumableData data = (ConsumableData) arcadiaItem.getItemData().getData();

        if (!playerData.eat(data.hungerCost())) {
            playerData.getSender().sendError("You are not hungry enough to eat this!");
            return;
        }
        item.setAmount(item.getAmount() - 1);
        Player player = playerData.getPlayer();
        player.playSound(player, Sound.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 1, (float) Math.floor(Math.random() * 0.15) + 0.85f);
        data.eat(playerData);
        player.sendMessage("You ate one " + arcadiaItem.getItemData().getName() + ". Yum!");
    }
}