package com.datasiqn.arcadia.players;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.DamageHelper;
import com.datasiqn.arcadia.items.ArcadiaItem;
import com.datasiqn.arcadia.items.stats.AttributeInstance;
import com.datasiqn.arcadia.items.stats.ItemAttribute;
import com.datasiqn.arcadia.items.stats.ItemStats;
import com.datasiqn.arcadia.items.stats.StatIcon;
import com.datasiqn.arcadia.items.type.ItemType;
import com.datasiqn.arcadia.util.XPUtil;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PlayerData {
    public static final double HEALTH_PRECISION = 0.00001;

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final File dataFile;
    private final PlayerEquipment equipment = new PlayerEquipment();
    private final ArcadiaSender<Player> player;
    private final Object2DoubleMap<PlayerAttribute> attributes = new Object2DoubleOpenHashMap<>();
    private final Arcadia plugin;
    private long xp = 0;

    private double health;
    private double hunger;
    private boolean debugMode;

    private BukkitTask regenHealthRunnable;
    private BukkitTask getHungryRunnable;

    private PlayerData(ArcadiaSender<Player> player, Arcadia plugin) {
        this.player = player;
        this.plugin = plugin;

        this.health = PlayerAttribute.MAX_HEALTH.getDefaultValue();
        this.hunger = PlayerAttribute.MAX_HUNGER.getDefaultValue();

        for (PlayerAttribute attribute : PlayerAttribute.values()) {
            attributes.put(attribute, attribute.getDefaultValue());
        }

        File dataFile = createDataFile();
        if (dataFile == null) {
            plugin.getLogger().warning("An IO error occurred when creating the data file for " + player.get().getName() + " (" + player.get().getUniqueId() + ")");
            this.dataFile = null;
            return;
        }
        this.dataFile = dataFile;
    }

    public void updateLevel() {
        player.get().setLevel(getLevel());
        player.get().setExp((float) getLevelProgress());
    }

    public void updateValues() {
        boolean regenHealth = health == getAttribute(PlayerAttribute.MAX_HEALTH);

        for (PlayerAttribute attribute : PlayerAttribute.values()) {
            attributes.put(attribute, attribute.getDefaultValue());
        }

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ArcadiaItem arcadiaItem = equipment.getItem(slot);

            if (arcadiaItem.getItemData().getItemType().getSlot() == slot) {
                ItemStats itemStats = arcadiaItem.getItemMeta().getItemStats();
                for (PlayerAttribute attribute : PlayerAttribute.values()) {
                    AttributeInstance itemAttribute = itemStats.getAttribute(attribute.getItemAttribute());
                    attributes.put(attribute, getAttribute(attribute) + (itemAttribute == null ? 0 : itemAttribute.getValue()));
                }
            }
        }

        for (ArcadiaItem item : equipment.getAmulet()) {
            if (item == null) continue;
            if (item.getItemData().getItemType() != ItemType.POWER_STONE) continue;
            for (PlayerAttribute attribute : PlayerAttribute.values()) {
                AttributeInstance itemAttribute = item.getItemMeta().getItemStats().getAttribute(attribute.getItemAttribute());
                attributes.put(attribute, getAttribute(attribute) + (itemAttribute == null ? 0 : itemAttribute.getValue()));
            }
        }

        if (regenHealth) health = getAttribute(PlayerAttribute.MAX_HEALTH);
        if (health > getAttribute(PlayerAttribute.MAX_HEALTH)) health = getAttribute(PlayerAttribute.MAX_HEALTH);
        Objects.requireNonNull(player.get().getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(getMaxHearts());

        if (!player.get().isDead()) {
            if (health <= 0) {
                player.get().setHealth(0);
                return;
            }

            double expectedHealth = getHearts();
            if (Math.abs(player.get().getHealth() - expectedHealth) > HEALTH_PRECISION) {
                if (debugMode) player.sendDebugMessage("Invalid health! Got " + player.get().getHealth() + ", expected " + expectedHealth);
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
        double damage = trueDamage ? rawDamage : DamageHelper.getFinalDamageWithDefense(rawDamage, getAttribute(PlayerAttribute.DEFENSE));
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

        if (health < getAttribute(PlayerAttribute.MAX_HEALTH)) {
            if (regenHealthRunnable == null || regenHealthRunnable.isCancelled()) {
                regenHealthRunnable = Bukkit.getScheduler().runTaskTimer(plugin, () -> heal(getAttribute(PlayerAttribute.MAX_HEALTH) / 10), 80, 80);
            }
        }

        if (!debugMode) return;
        player.sendMessageRaw("-------------------------");
        player.sendMessageRaw(ChatColor.GOLD + "Damage Summary:");
        player.sendDebugMessage("Raw damage dealt: " + ChatColor.RED + rawDamage);
        if (event.getDamage() != event.getFinalDamage()) player.sendDebugMessage(ChatColor.RED + "Damage and raw damage do not match!");
        DecimalFormat format = new DecimalFormat("#.##");
        final String sDefense = format.format(DamageHelper.getDamageReduction(getAttribute(PlayerAttribute.DEFENSE)) * 100) + "% (" + getAttribute(PlayerAttribute.DEFENSE) + StatIcon.DEFENSE + ")";
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
        heal(getAttribute(PlayerAttribute.MAX_HEALTH));
    }
    public void heal(double amount) {
        health += amount;
        if (health >= getAttribute(PlayerAttribute.MAX_HEALTH)) {
            health = getAttribute(PlayerAttribute.MAX_HEALTH);
            if (regenHealthRunnable != null) regenHealthRunnable.cancel();
        }
        player.get().setHealth(getHearts());
        updateActionbar();
    }

    public boolean eat(double filling) {
        if (filling > hunger) return false;
        hunger -= filling;
        if (hunger < getAttribute(PlayerAttribute.MAX_HUNGER) && (getHungryRunnable == null || getHungryRunnable.isCancelled())) getHungryRunnable = Bukkit.getScheduler().runTaskTimer(plugin, () -> hungerTick(5), 80, 80);
        updateActionbar();
        return true;
    }

    public void hungerTick(double amount) {
        hunger = Math.min(hunger + amount, getAttribute(PlayerAttribute.MAX_HUNGER));
        if (hunger == getAttribute(PlayerAttribute.MAX_HUNGER)) getHungryRunnable.cancel();
        updateActionbar();
    }

    public void updateActionbar() {
        DecimalFormat format = new DecimalFormat("#");
        String displayHealth = formatDouble(health, format) + "/" + formatDouble(getAttribute(PlayerAttribute.MAX_HEALTH), format) + ItemAttribute.HEALTH.getIcon();
        String displayDefense = formatDouble(getAttribute(PlayerAttribute.DEFENSE), format) + ItemAttribute.DEFENSE.getIcon();
        String displayStrength = formatDouble(getAttribute(PlayerAttribute.STRENGTH), format) + ItemAttribute.STRENGTH.getIcon();
        String displayHunger = formatDouble(hunger, format) + "/" + formatDouble(getAttribute(PlayerAttribute.MAX_HUNGER), format) + ItemAttribute.HUNGER.getIcon();
        player.get().spigot().sendMessage(ChatMessageType.ACTION_BAR,
                new ComponentBuilder()
                        .append(displayHealth + " ").color(ItemAttribute.HEALTH.getColor())
                        .append(displayDefense + " ").color(ItemAttribute.DEFENSE.getColor())
                        .append(displayStrength + " ").color(ItemAttribute.STRENGTH.getColor())
                        .append(displayHunger).color(ItemAttribute.HUNGER.getColor())
                        .create());
    }

    public void loadData() {
        JsonObject jsonObject;
        try {
            FileReader reader = new FileReader(dataFile);
            JsonElement jsonElement = JsonParser.parseReader(reader);
            jsonObject = jsonElement.getAsJsonObject();
        } catch (FileNotFoundException e) {
            plugin.getLogger().warning("Data file for player " + player.get().getName() + " (" + player.get().getUniqueId() + ") does not exist");
            return;
        }
        xp = 0;
        if (jsonObject.has("xp")) {
            xp = jsonObject.get("xp").getAsLong();
        }

        if (jsonObject.has("amulet")) {
            JsonArray amuletSection = jsonObject.get("amulet").getAsJsonArray();
            for (JsonElement element : amuletSection) {
                JsonObject amuletItem = element.getAsJsonObject();
                int index = amuletItem.get("slot").getAsInt();
                ArcadiaItem item = ArcadiaItem.deserialize(gson.fromJson(amuletItem.get("item").getAsJsonObject(), new TypeToken<Map<String, Object>>() {}.getType()));
                equipment.getAmulet()[index] = item;
            }
        }
        plugin.getLogger().info("Loaded player data for " + player.get().getName() + " (" + player.get().getUniqueId() + ")");
    }

    public void saveData() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("xp", new JsonPrimitive(xp));
        ArcadiaItem[] amulet = equipment.getAmulet();
        JsonArray amuletArray = new JsonArray();
        for (int i = 0; i < amulet.length; i++) {
            if (amulet[i] == null) continue;
            JsonObject amuletItem = new JsonObject();
            amuletItem.add("slot", new JsonPrimitive(i));
            amuletItem.add("item", gson.toJsonTree(amulet[i].serialize()));
            amuletArray.add(amuletItem);
        }
        jsonObject.add("amulet", amuletArray);
        try {
            FileWriter writer = new FileWriter(dataFile);
            gson.toJson(jsonObject, writer);
            writer.close();
            plugin.getLogger().info("Saved data for " + player.get().getName() + " (" + player.get().getUniqueId() + ")");
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save data for " + player.get().getName() + " (" + player.get().getUniqueId() + ")");
        }
    }

    public PlayerEquipment getEquipment() {
        return equipment;
    }

    public long getTotalXp() {
        return xp;
    }

    public int getLevel() {
        return XPUtil.getLevelFromXP(xp);
    }

    public double getLevelProgress() {
        return XPUtil.getProgress(xp);
    }

    public void setTotalXp(long xp) {
        this.xp = xp;
        saveData();
        updateLevel();
    }

    public double getStrength() {
        return getAttribute(PlayerAttribute.STRENGTH);
    }

    public double getAttackSpeed() {
        return getAttribute(PlayerAttribute.ATTACK_SPEED);
    }

    public double getAttribute(PlayerAttribute attribute) {
        return attributes.getDouble(attribute);
    }

    public Player getPlayer() {
        return player.get();
    }

    public ArcadiaSender<Player> getSender() {
        return player;
    }

    public UUID getUniqueId() {
        return player.get().getUniqueId();
    }

    public boolean inDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public void toggleDebugMode() {
        setDebugMode(!debugMode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayerData that = (PlayerData) o;

        return getPlayer().getUniqueId().equals(that.getPlayer().getUniqueId());
    }

    public static @NotNull PlayerData create(@NotNull ArcadiaSender<Player> player, Arcadia plugin) {
        PlayerData stats = new PlayerData(player, plugin);
        stats.updateValues();
        return stats;
    }

    private double getHearts() {
        return health / getAttribute(PlayerAttribute.MAX_HEALTH) * Objects.requireNonNull(player.get().getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
    }

    private double getMaxHearts() {
        return Math.floor(Math.min(Math.max(0.01 * getAttribute(PlayerAttribute.MAX_HEALTH) + 19.9, 20), 40) / 2) * 2;
    }

    private @Nullable File createDataFile() {
        File file = new File(plugin.getPlayerManager().getDataFolder().getPath(), player.get().getUniqueId() + ".json");
        try {
            if (file.createNewFile()) {
                FileWriter writer = new FileWriter(file);
                JsonObject jsonObject = new JsonObject();
                jsonObject.add("xp", new JsonPrimitive(0));
                jsonObject.add("amulet", new JsonArray());
                gson.toJson(jsonObject, writer);
                writer.close();
            }
        } catch (IOException e) {
            return null;
        }
        return file;
    }

    private static String formatDouble(double d, @NotNull DecimalFormat format) {
        return format.format(Math.ceil(d));
    }
}
