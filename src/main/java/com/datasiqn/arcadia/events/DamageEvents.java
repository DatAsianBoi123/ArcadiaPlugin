package com.datasiqn.arcadia.events;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaKeys;
import com.datasiqn.arcadia.DamageHelper;
import com.datasiqn.arcadia.enchants.DamageModifierType;
import com.datasiqn.arcadia.enchants.EnchantType;
import com.datasiqn.arcadia.enchants.modifiers.DamageEnchantModifier;
import com.datasiqn.arcadia.enchants.modifiers.EnchantModifier;
import com.datasiqn.arcadia.enchants.modifiers.EntityEnchantModifier;
import com.datasiqn.arcadia.entities.ArcadiaEntity;
import com.datasiqn.arcadia.entities.ArcadiaHostileEntity;
import com.datasiqn.arcadia.items.ArcadiaItem;
import com.datasiqn.arcadia.items.meta.ArcadiaItemMeta;
import com.datasiqn.arcadia.items.stats.AttributeInstance;
import com.datasiqn.arcadia.items.stats.ItemAttribute;
import com.datasiqn.arcadia.items.type.ItemType;
import com.datasiqn.arcadia.players.ArcadiaSender;
import com.datasiqn.arcadia.players.PlayerData;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class DamageEvents implements Listener {
    private final Arcadia plugin;

    public DamageEvents(Arcadia plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamage(@NotNull EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
            event.setCancelled(true);
            return;
        }
        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK || event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) return;

        if (((CraftEntity) event.getEntity()).getHandle() instanceof ArcadiaEntity entity) {
            entity.damage(event.getDamage(), event);
            spawnDamageIndicator(event.getEntity().getLocation(), event.getDamage());
        } else if (event.getEntity() instanceof Player player) {
            plugin.getPlayerManager().getPlayerData(player).damage(event, event.getCause() == EntityDamageEvent.DamageCause.FALL);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(@NotNull EntityDamageByEntityEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) return;
        if (((CraftEntity) event.getEntity()).getHandle() instanceof ArcadiaEntity entity) {
            double damage = calcDamage(event, entity);
            entity.damage(damage, event);
            spawnDamageIndicator(event.getEntity().getLocation(), damage);

            if (!(event.getDamager() instanceof Player player)) return;

            PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
            ArcadiaItem itemInMainHand = playerData.getEquipment().getItemInMainHand();
            if (itemInMainHand.getItemData().getItemType() != ItemType.SWORD) return;

            double attackSpeed = playerData.getAttackSpeed();
            plugin.runAfterOneTick(() -> {
                Entity eventEntity = event.getEntity();
                if (!(eventEntity instanceof LivingEntity living)) return;
                living.setNoDamageTicks((int) Math.round(20 - attackSpeed / 5));
            });
        } else if (event.getEntity() instanceof Player player) {
            event.setDamage(calcPlayerDamage(event));
            plugin.getPlayerManager().getPlayerData(player).damage(event);
        }
    }

    private void spawnDamageIndicator(@NotNull Location center, double damage) {
        World world = center.getWorld();
        if (world == null) return;
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.ENGLISH);
        AreaEffectCloud entity = world.spawn(center, AreaEffectCloud.class, cloud -> {
            Location location = cloud.getLocation();
            Vector direction = new Vector(Math.random() - 0.5, 0, Math.random() - 0.5);
            direction.normalize();
            direction.setY(0.5);
            location.add(direction);
            cloud.teleport(location);
            cloud.setCustomName(ChatColor.RED + numberFormat.format(Math.round(damage)));
            cloud.setCustomNameVisible(true);
            cloud.setParticle(Particle.BLOCK_CRACK, Material.AIR.createBlockData());
        });
        Bukkit.getScheduler().runTaskLater(Arcadia.getProvidingPlugin(Arcadia.class), entity::remove, 20);
    }

    private double calcPlayerDamage(@NotNull EntityDamageByEntityEvent event) {
        double damage = event.getDamage();

        if (!(((CraftEntity) event.getDamager()).getHandle() instanceof ArcadiaHostileEntity hostileEntity)) return damage;

        return hostileEntity.getDamage();
    }

    private double calcDamage(@NotNull EntityDamageByEntityEvent event, ArcadiaEntity entity) {
        double damage = event.getDamage();

        if (event.getDamager() instanceof Arrow arrow) {
            double arrowDamage = calcArrowDamage(arrow, damage);
            if (arrow.getShooter() instanceof Player player) {
                PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
                if (playerData.inDebugMode()) sendDebugInfo(playerData.getPlayer(), arrowDamage, -1, arrowDamage, 1, 1);
            }
            return arrowDamage;
        }

        if (!(event.getDamager() instanceof Player player)) return damage;

        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        ArcadiaItem item = playerData.getEquipment().getItemInMainHand();

        if (item.getItemData().getItemType() != ItemType.SWORD) return damage;
        ArcadiaItemMeta itemMeta = item.getItemMeta();
        AttributeInstance damageAttribute = itemMeta.getItemStats().getAttribute(ItemAttribute.DAMAGE);
        if (damageAttribute == null) return damage;

        double finalDamage;

        double strength = playerData.getStrength();

        double additiveBonus = 1;
        double multiplicativeBonus = 1;
        for (Map.Entry<EnchantType, Integer> enchantData : itemMeta.getEnchants().entrySet()) {
            EnchantModifier modifier = enchantData.getKey().getEnchantment().getModifier();
            if (modifier instanceof DamageEnchantModifier damageModifier) {
                double multiplier = damageModifier.getMultiplier(enchantData.getValue());
                if (damageModifier.getType() == DamageModifierType.ADDITIVE_MULTIPLIER) {
                    additiveBonus += multiplier;
                } else if (damageModifier.getType() == DamageModifierType.MULTIPLICATIVE_MULTIPLIER) {
                    multiplicativeBonus *= multiplier;
                }
                continue;
            }

            if (modifier instanceof EntityEnchantModifier enchantModifier) {
                enchantModifier.modifyEntity(entity, enchantData.getValue());
            }
        }

        double damageValue = damageAttribute.getValue();
        finalDamage = (damageValue + DamageHelper.getStrengthBonus(strength, damageValue)) * (additiveBonus * multiplicativeBonus);

        if (playerData.inDebugMode()) {
            sendDebugInfo(playerData.getPlayer(), damageValue, strength, finalDamage, additiveBonus, multiplicativeBonus);
        }

        return finalDamage;
    }

    private double calcArrowDamage(@NotNull Arrow arrow, double defaultDamage) {
        PersistentDataContainer pdc = arrow.getPersistentDataContainer();
        return pdc.getOrDefault(ArcadiaKeys.ARROW_DAMAGE, PersistentDataType.DOUBLE, defaultDamage);
    }

    private void sendDebugInfo(@NotNull ArcadiaSender<Player> sender, double rawDamage, double strength, double finalDamage, double additiveMultiplier, double multiplicativeMultiplier) {
        sender.sendMessageRaw("-------------------------");
        sender.sendMessageRaw(ChatColor.GOLD + "Damage Summary:");
        DecimalFormat format = new DecimalFormat("#.###");
        sender.sendDebugMessage("Raw damage dealt: " + ChatColor.RED + format.format(rawDamage));
        if (strength >= 0) {
            sender.sendDebugMessage("Strength: " + ChatColor.RED + format.format(strength));
            sender.sendDebugMessage("Strength Bonus: " + ChatColor.RED + format.format(DamageHelper.getStrengthBonus(strength, rawDamage)));
        }
        sender.sendDebugMessage("Multiplier: " + ChatColor.RED + format.format(additiveMultiplier * multiplicativeMultiplier) + " (" + format.format(additiveMultiplier) + " * " + format.format(multiplicativeMultiplier) + ")");
        sender.sendDebugMessage("Final damage: " + ChatColor.RED + format.format(finalDamage));
        sender.sendMessageRaw("-------------------------");
    }
}
