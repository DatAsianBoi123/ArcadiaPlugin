package com.datasiqn.arcadia.listeners;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaTag;
import com.datasiqn.arcadia.DamageHelper;
import com.datasiqn.arcadia.dungeon.DungeonPlayer;
import com.datasiqn.arcadia.enchants.DamageModifierType;
import com.datasiqn.arcadia.enchants.EnchantType;
import com.datasiqn.arcadia.enchants.modifiers.DamageEnchantModifier;
import com.datasiqn.arcadia.enchants.modifiers.EnchantModifier;
import com.datasiqn.arcadia.enchants.modifiers.EntityEnchantModifier;
import com.datasiqn.arcadia.entities.ArcadiaEntity;
import com.datasiqn.arcadia.entities.ArcadiaHostileEntity;
import com.datasiqn.arcadia.item.ArcadiaItem;
import com.datasiqn.arcadia.item.meta.ArcadiaItemMeta;
import com.datasiqn.arcadia.item.stat.AttributeInstance;
import com.datasiqn.arcadia.item.stat.ItemAttribute;
import com.datasiqn.arcadia.managers.UpgradeEventManager;
import com.datasiqn.arcadia.player.ArcadiaSender;
import com.datasiqn.arcadia.player.PlayerData;
import com.datasiqn.arcadia.upgrade.listeners.actions.DamageEnemyAction;
import com.datasiqn.arcadia.upgrade.listeners.actions.KillEnemyAction;
import com.datasiqn.arcadia.util.PdcUtil;
import com.datasiqn.schedulebuilder.ScheduleBuilder;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class DamageListener implements Listener {
    private final Arcadia plugin;

    public DamageListener(Arcadia plugin) {
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

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(@NotNull EntityDamageByEntityEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) return;
        if (((CraftEntity) event.getEntity()).getHandle() instanceof ArcadiaEntity entity) {
            double damage = calcDamage(event, entity);
            if (!(event.getDamager() instanceof Player)) {
                entity.damage(damage, event);
                spawnDamageIndicator(event.getEntity().getLocation(), damage);
            }

            Player player;
            if (!(event.getDamager() instanceof Player damager)) {
                if (!(event.getDamager() instanceof Projectile projectile) || !(projectile.getShooter() instanceof Player shooter)) {
                    return;
                }
                player = shooter;
                if (event.getDamager() instanceof Player) {
                    entity.damage(damage, event);
                    spawnDamageIndicator(event.getEntity().getLocation(), damage);
                }
            } else {
                player = damager;
                ServerPlayer nmsPlayer = ((CraftPlayer) damager).getHandle();
                if (nmsPlayer.getAttackStrengthScale(0.5f) < 1) {
//                    try {
//                        Field attackStrengthTicker = net.minecraft.world.entity.LivingEntity.class.getDeclaredField("aO" /* attackStrengthTicker */);
//                        attackStrengthTicker.setAccessible(true);
//                        int strength = attackStrengthTicker.getInt(nmsPlayer);
//                        ScheduleBuilder.create()
//                                .executes(runnable -> {
//                                    try {
//                                        attackStrengthTicker.setInt(nmsPlayer, strength);
//                                    } catch (IllegalAccessException e) {
//                                        e.printStackTrace();
//                                    }
//                                }).run(plugin);
//                    } catch (NoSuchFieldException | IllegalAccessException e) {
//                        e.printStackTrace();
//                    }

                    event.setCancelled(true);
                    return;
                }
                entity.damage(damage, event);
                spawnDamageIndicator(event.getEntity().getLocation(), damage);
            }

            PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);

            DungeonPlayer dungeonPlayer = plugin.getDungeonManager().getDungeonPlayer(playerData.getUniqueId());
            if (dungeonPlayer != null) {
                UpgradeEventManager eventManager = plugin.getUpgradeEventManager();
                eventManager.emit(new DamageEnemyAction(dungeonPlayer, entity, damage));

                if (entity.arcadia().health() <= damage) eventManager.emit(new KillEnemyAction(dungeonPlayer, entity));
            }

            ScheduleBuilder.create().executes(runnable -> {
                Entity eventEntity = event.getEntity();
                if (!(eventEntity instanceof LivingEntity living)) return;
                living.setNoDamageTicks(0);
            }).run(plugin);
        } else if (event.getEntity() instanceof Player player) {
            event.setDamage(calcPlayerDamage(event));
            plugin.getPlayerManager().getPlayerData(player).damage(event);
        }
    }

    private void spawnDamageIndicator(@NotNull Location center, double damage) {
        World world = center.getWorld();
        if (world == null) return;
        Vector direction = new Vector(Math.random() - 0.5, 0, Math.random() - 0.5);
        direction.normalize();
        direction.setY(1);
        center.add(direction);
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.ENGLISH);
        TextDisplay entity = world.spawn(center, TextDisplay.class, display -> {
            display.setText(ChatColor.RED + numberFormat.format(Math.round(damage)));
            display.setAlignment(TextDisplay.TextAlignment.CENTER);
            display.setBillboard(Display.Billboard.CENTER);
        });
        ScheduleBuilder.create()
                .wait(1.0).seconds()
                .executes(runnable -> entity.remove())
                .run(plugin);
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
                if (playerData.inDebugMode()) sendDebugInfo(playerData.getSender(), arrowDamage, -1, arrowDamage, 1, 1);
            }
            return arrowDamage;
        }

        if (!(event.getDamager() instanceof Player player)) return damage;

        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        ArcadiaItem item = playerData.getEquipment().getItemInMainHand();

        if (item.getItemData().getItemType().getSlot() != EquipmentSlot.HAND) return damage;
        ArcadiaItemMeta itemMeta = item.getItemMeta();
        AttributeInstance damageAttribute = itemMeta.getItemStats().getAttribute(ItemAttribute.DAMAGE);
        if (damageAttribute == null) return damage;

        double finalDamage;

        double strength = playerData.getStrength();

        double additiveBonus = 1;
        double multiplicativeBonus = 1;
        for (EnchantType enchantType : itemMeta.getEnchants()) {
            int level = itemMeta.getEnchantLevel(enchantType);
            EnchantModifier modifier = enchantType.getEnchantment().getModifier();
            if (modifier instanceof DamageEnchantModifier damageModifier) {
                double multiplier = damageModifier.getMultiplier(level);
                if (damageModifier.getType() == DamageModifierType.ADDITIVE_MULTIPLIER) {
                    additiveBonus += multiplier;
                } else if (damageModifier.getType() == DamageModifierType.MULTIPLICATIVE_MULTIPLIER) {
                    multiplicativeBonus *= multiplier;
                }
                continue;
            }

            if (modifier instanceof EntityEnchantModifier enchantModifier) {
                enchantModifier.modifyEntity(entity, level);
            }
        }

        double damageValue = damageAttribute.getValue();
        finalDamage = (damageValue + DamageHelper.getStrengthBonus(strength, damageValue)) * (additiveBonus * multiplicativeBonus);

        if (playerData.inDebugMode()) {
            sendDebugInfo(playerData.getSender(), damageValue, strength, finalDamage, additiveBonus, multiplicativeBonus);
        }

        return finalDamage;
    }

    private double calcArrowDamage(@NotNull Arrow arrow, double defaultDamage) {
        PersistentDataContainer pdc = arrow.getPersistentDataContainer();
        return PdcUtil.getOrDefault(pdc, ArcadiaTag.ARROW_DAMAGE, defaultDamage);
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
