package com.datasiqn.arcadia.events;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaKeys;
import com.datasiqn.arcadia.DamageHelper;
import com.datasiqn.arcadia.items.ArcadiaItem;
import com.datasiqn.arcadia.items.ItemType;
import com.datasiqn.arcadia.items.stats.ItemAttribute;
import com.datasiqn.arcadia.players.PlayerStats;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class PlayerEvents implements Listener {
    private final Arcadia plugin;

    public PlayerEvents(Arcadia plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        AttributeInstance attribute = event.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        assert attribute != null;
        attribute.setBaseValue(16);

        plugin.getPlayerManager().getPlayerData(event.getPlayer()).playerStats().updateValues();
    }

    @EventHandler
    public void onPlayerRespawn(@NotNull PlayerRespawnEvent event) {
        plugin.getPlayerManager().getPlayerData(event.getPlayer()).playerStats().heal();
    }

    @EventHandler
    public void onEntityRegen(@NotNull EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerLoseHunger(@NotNull FoodLevelChangeEvent event) {
        event.setCancelled(true);
        event.getEntity().setFoodLevel(20);
    }

    @EventHandler
    public void onPlayerLeave(@NotNull PlayerQuitEvent event) {
        AttributeInstance attackSpeedAttribute = event.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        assert attackSpeedAttribute != null;
        attackSpeedAttribute.setBaseValue(4);

        AttributeInstance healthAttribute = event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH);
        assert healthAttribute != null;
        healthAttribute.setBaseValue(20);

        plugin.getPlayerManager().removePlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerShootBow(@NotNull EntityShootBowEvent event) {
        if (event.getBow() == null) return;
        if (!(event.getEntity() instanceof Player player)) return;

        PlayerStats playerStats = plugin.getPlayerManager().getPlayerData(player).playerStats();

        ArcadiaItem bowItem = new ArcadiaItem(event.getBow());
        if (bowItem.getItemData().getItemType() != ItemType.BOW) return;

        PersistentDataContainer pdc = event.getProjectile().getPersistentDataContainer();
        com.datasiqn.arcadia.items.stats.AttributeInstance damageAttribute = bowItem.getItemMeta().getItemStats().getAttribute(ItemAttribute.DAMAGE);
        double damageValue = damageAttribute == null ? 1 : damageAttribute.getValue();
        pdc.set(ArcadiaKeys.ARROW_DAMAGE, PersistentDataType.DOUBLE, damageValue * DamageHelper.getStrengthMultiplier(playerStats.getStrength()) * event.getForce());
    }
}
