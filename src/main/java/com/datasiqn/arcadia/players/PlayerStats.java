package com.datasiqn.arcadia.players;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.DamageHelper;
import com.datasiqn.arcadia.items.ArcadiaItem;
import com.datasiqn.arcadia.items.stats.AttributeInstance;
import com.datasiqn.arcadia.items.stats.ItemAttribute;
import com.datasiqn.arcadia.items.stats.ItemStats;
import com.datasiqn.arcadia.items.stats.StatIcon;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Objects;

public class PlayerStats {
    @SuppressWarnings("FieldCanBeLocal")
    private final double HEALTH_PRECISION = 0.00001;

    private final ArcadiaSender<Player> player;
    private final Arcadia plugin;
    private final double defaultHealth = 10;
    private double health;
    private double maxHealth;
    private double defense;
    private double strength;

    private BukkitTask regenHealthRunnable;

    private PlayerStats(ArcadiaSender<Player> player, Arcadia plugin) {
        this.player = player;
        this.plugin = plugin;
        this.health = defaultHealth;
        this.maxHealth = defaultHealth;
        this.defense = 0;
        this.strength = 0;
    }

    public void updateValues() {
        boolean regenHealth = health == maxHealth;
        double totalDefense = 0;
        double totalHealth = defaultHealth;
        double totalStrength = 0;

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            EntityEquipment equipment = player.get().getEquipment();
            if (equipment == null) continue;

            ItemStack itemStack = equipment.getItem(slot);
            ArcadiaItem arcadiaItem = new ArcadiaItem(itemStack);

            if (arcadiaItem.getItemData().getItemType().getSlot() == slot) {
                ItemStats itemStats = arcadiaItem.getItemMeta().getItemStats();
                AttributeInstance defenseAttribute = itemStats.getAttribute(ItemAttribute.DEFENSE);
                totalDefense += defenseAttribute == null ? 0 : defenseAttribute.getValue();

                AttributeInstance healthAttribute = itemStats.getAttribute(ItemAttribute.HEALTH);
                totalHealth += healthAttribute == null ? 0 : healthAttribute.getValue();

                AttributeInstance strengthAttribute = itemStats.getAttribute(ItemAttribute.STRENGTH);
                totalStrength += strengthAttribute == null ? 0 : strengthAttribute.getValue();
            }
        }

        defense = totalDefense;
        maxHealth = totalHealth;
        strength = totalStrength;
        if (regenHealth) health = maxHealth;
        if (health > maxHealth) health = maxHealth;
        Objects.requireNonNull(player.get().getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(getMaxHearts());

        if (!player.get().isDead()) {
            if (health <= 0) {
                player.get().setHealth(0);
                return;
            }

            double expectedHealth = getHearts();
            if (Math.abs(player.get().getHealth() - expectedHealth) > HEALTH_PRECISION) {
                if (plugin.inDebugMode(player.get().getUniqueId())) player.sendDebugMessage("Invalid health! Got " + player.get().getHealth() + ", expected " + expectedHealth);
                player.get().setHealth(expectedHealth);
            }
        }
    }

    public void damage(@NotNull EntityDamageEvent event) {
        damage(event, false);
    }
    @SuppressWarnings("deprecation")
    public void damage(@NotNull EntityDamageEvent event, boolean trueDamage) {
        double rawDamage = event.getDamage();
        double damage = trueDamage ? rawDamage : DamageHelper.getFinalDamageWithDefense(rawDamage, defense);
        if (player.get().isBlocking()) damage = 0;
        health -= damage;
        if (health <= 0) health = 0;
        double hearts = getHearts();
        event.setDamage(player.get().getHealth() - hearts);
        for (EntityDamageEvent.DamageModifier modifier : EntityDamageEvent.DamageModifier.values()) {
            if (!event.isApplicable(modifier)) continue;
            if (modifier == EntityDamageEvent.DamageModifier.BASE || modifier == EntityDamageEvent.DamageModifier.BLOCKING) continue;
            event.setDamage(modifier, 0);
        }
        updateActionbar();

        if (health < maxHealth) {
            if (regenHealthRunnable == null || regenHealthRunnable.isCancelled()) {
                regenHealthRunnable = Bukkit.getScheduler().runTaskTimer(Arcadia.getProvidingPlugin(Arcadia.class), () -> heal(maxHealth / 10), 80, 80);
            }
        }

        if (!plugin.inDebugMode(player.get().getUniqueId())) return;
        player.sendMessageRaw("-------------------------");
        player.sendMessageRaw(ChatColor.GOLD + "Damage Summary:");
        player.sendDebugMessage("Raw damage dealt: " + ChatColor.RED + rawDamage);
        if (event.getDamage() != event.getFinalDamage()) player.sendDebugMessage(ChatColor.RED + "Damage and raw damage do not match!");
        DecimalFormat format = new DecimalFormat("#.##");
        final String sDefense = format.format(DamageHelper.getDamageReduction(defense) * 100) + "% (" + defense + StatIcon.DEFENSE + ")";
        if (trueDamage) {
            player.sendDebugMessage("Damage reduction (ignored, true damage): " + ChatColor.GREEN + sDefense);
        } else {
            player.sendDebugMessage("Damage reduction: " + ChatColor.GREEN + sDefense);
        }
        player.sendDebugMessage("Final damage: " + ChatColor.RED + damage);
        player.sendDebugMessage("Damage to hearts: " + ChatColor.RED + (player.get().getHealth() - hearts));
        player.sendDebugMessage("Final hearts: " + ChatColor.GREEN + hearts);
        player.sendMessageRaw("-------------------------");
    }

    public void heal() {
        heal(maxHealth);
    }
    public void heal(double amount) {
        health += amount;
        if (health >= maxHealth) {
            health = maxHealth;
            if (regenHealthRunnable != null) regenHealthRunnable.cancel();
        }
        player.get().setHealth(getHearts());
        updateActionbar();
    }

    public void updateActionbar() {
        DecimalFormat format = new DecimalFormat("#");
        String displayHealth = ChatColor.RED + formatDouble(health, format) + "/" + formatDouble(maxHealth, format) + StatIcon.HEALTH;
        String displayDefense = ChatColor.GREEN + formatDouble(defense, format) + StatIcon.DEFENSE;
        String displayStrength = ChatColor.DARK_RED + formatDouble(strength, format) + StatIcon.STRENGTH;
        player.get().spigot().sendMessage(ChatMessageType.ACTION_BAR,
                new TextComponent(displayHealth + " " + displayDefense + " " + displayStrength));
    }

    public double getStrength() {
        return strength;
    }

    public static @NotNull PlayerStats create(@NotNull ArcadiaSender<Player> player, Arcadia plugin) {
        PlayerStats stats = new PlayerStats(player, plugin);
        stats.updateValues();
        stats.health = stats.maxHealth;
        return stats;
    }

    private double getHearts() {
        return health / maxHealth * Objects.requireNonNull(player.get().getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
    }

    private double getMaxHearts() {
        return Math.floor(Math.min(Math.max(0.01 * maxHealth + 19.9, 20), 40) / 2) * 2;
    }

    private static String formatDouble(double d, @NotNull DecimalFormat format) {
        return format.format(Math.ceil(d));
    }
}
