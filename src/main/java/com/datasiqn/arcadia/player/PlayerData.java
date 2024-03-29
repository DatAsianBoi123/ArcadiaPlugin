package com.datasiqn.arcadia.player;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.DamageHelper;
import com.datasiqn.arcadia.amulet.Amulet;
import com.datasiqn.arcadia.amulet.PowerStone;
import com.datasiqn.arcadia.dungeon.DungeonPlayer;
import com.datasiqn.arcadia.item.ArcadiaItem;
import com.datasiqn.arcadia.item.stat.AttributeInstance;
import com.datasiqn.arcadia.item.stat.ItemAttribute;
import com.datasiqn.arcadia.item.stat.ItemStats;
import com.datasiqn.arcadia.item.stat.StatIcon;
import com.datasiqn.arcadia.upgrade.actions.UpdateAttributesAction;
import com.datasiqn.resultapi.Result;
import com.datasiqn.schedulebuilder.ScheduleBuilder;
import com.google.gson.*;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.text.DecimalFormat;
import java.util.UUID;

public class PlayerData {
    public static final double DEFAULT_ATTACK_SPEED = 16;
    public static final double HEALTH_PRECISION = 0.00001;

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final Arcadia plugin;
    private final File dataFile;
    private final PlayerEquipment equipment = new PlayerEquipment(this);
    private final ArcadiaSender<Player> sender;
    private final Player player;
    private final Object2DoubleMap<PlayerAttribute> attributes = new Object2DoubleOpenHashMap<>();
    private final Experience xp = new Experience();

    private double health;
    private double hunger;
    private boolean debugMode;

    private BukkitTask regenHealthRunnable;
    private BukkitTask getHungryRunnable;

    private PlayerData(@NotNull ArcadiaSender<Player> sender, Arcadia plugin) {
        this.sender = sender;
        this.player = sender.get();
        this.plugin = plugin;

        this.health = PlayerAttribute.MAX_HEALTH.getDefaultValue();
        this.hunger = PlayerAttribute.MAX_HUNGER.getDefaultValue();

        for (PlayerAttribute attribute : PlayerAttribute.values()) {
            attributes.put(attribute, attribute.getDefaultValue());
        }

        File dataFile = createDataFile();
        if (dataFile == null) {
            plugin.getLogger().warning("An IO error occurred when creating the data file for " + player.getName() + " (" + player.getUniqueId() + ")");
            this.dataFile = null;
            return;
        }
        this.dataFile = dataFile;
    }

    public void updateLevel() {
        player.setLevel(xp.getLevel());
        player.setExp((float) xp.getProgress());
    }

    public void updateValues() {
        boolean regenHealth = health == getAttribute(PlayerAttribute.MAX_HEALTH);

        for (PlayerAttribute attribute : PlayerAttribute.values()) {
            attributes.put(attribute, attribute.getDefaultValue());
        }

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ArcadiaItem arcadiaItem = equipment.getItem(slot);

            if (arcadiaItem.getData().getType().getSlot() == slot) {
                ItemStats itemStats = arcadiaItem.getItemMeta().getItemStats();
                for (PlayerAttribute attribute : PlayerAttribute.values()) {
                    AttributeInstance itemAttribute = itemStats.getAttribute(attribute.getItemAttribute());
                    attributes.mergeDouble(attribute, itemAttribute == null ? 0 : itemAttribute.getValue(), Double::sum);
                }
            }
        }

        for (PowerStone powerStone : equipment.getAmulet()) {
            if (powerStone == null) continue;
            for (PlayerAttribute attribute : PlayerAttribute.values()) {
                double attributeValue = powerStone.getData().getAttribute(attribute);
                attributes.mergeDouble(attribute, attributeValue, Double::sum);
            }
        }

        DungeonPlayer dungeonPlayer = plugin.getDungeonManager().getDungeonPlayer(this);
        if (dungeonPlayer != null) {
            plugin.getUpgradeEventManager().emit(new UpdateAttributesAction(dungeonPlayer, attributes, plugin));
        }

        var attackSpeedAttribute = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if (attackSpeedAttribute != null) {
            ScheduleBuilder.create()
                    .executes(runnable -> {
                        double currentSpeed = equipment.getItemInMainHand().getData().getType().getAttackSpeed();
                        double newSpeed = currentSpeed * Math.pow(DEFAULT_ATTACK_SPEED / currentSpeed, getAttackSpeed() / 100);
                        attackSpeedAttribute.setBaseValue(newSpeed);
                    }).run(plugin);
        }

        if (regenHealth) health = getAttribute(PlayerAttribute.MAX_HEALTH);
        if (health > getAttribute(PlayerAttribute.MAX_HEALTH)) health = getAttribute(PlayerAttribute.MAX_HEALTH);
        var healthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (healthAttribute == null) return;
        healthAttribute.setBaseValue(getMaxHearts());

        if (!player.isDead()) {
            if (health <= 0) {
                player.setHealth(0);
                return;
            }

            double expectedHealth = getHearts();
            if (Math.abs(player.getHealth() - expectedHealth) > HEALTH_PRECISION) {
                if (debugMode) sender.sendDebugMessage("Invalid health! Got " + player.getHealth() + ", expected " + expectedHealth);
                player.setHealth(expectedHealth);
            }
        }

        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        serverPlayer.getAbilities().walkingSpeed = 0.1f * (float) getAttribute(PlayerAttribute.SPEED);
        serverPlayer.onUpdateAbilities();
        // idk why i can't just use the serverPlayer variable here, but it breaks if i do
        var moveSpeedAttribute = ((CraftPlayer) player).getHandle().getAttribute(Attributes.MOVEMENT_SPEED);
        if (moveSpeedAttribute != null) {
            moveSpeedAttribute.setBaseValue(serverPlayer.getAbilities().walkingSpeed);
        }

        beginRegenHealth();
    }

    public void damage(@NotNull EntityDamageEvent event) {
        damage(event, false);
    }
    @SuppressWarnings("deprecation")
    public void damage(@NotNull EntityDamageEvent event, boolean trueDamage) {
        double rawDamage = event.getDamage();
        double damage = trueDamage ? rawDamage : DamageHelper.getFinalDamageWithDefense(rawDamage, getAttribute(PlayerAttribute.DEFENSE));
        if (player.isBlocking()) damage = 0;
        health -= damage;
        if (health <= 0) health = 0;
        double hearts = getHearts();
        event.setDamage(player.getHealth() - hearts);
        for (EntityDamageEvent.DamageModifier modifier : EntityDamageEvent.DamageModifier.values()) {
            if (!event.isApplicable(modifier)) continue;
            if (modifier == EntityDamageEvent.DamageModifier.BASE || modifier == EntityDamageEvent.DamageModifier.BLOCKING) continue;
            event.setDamage(modifier, 0);
        }
        updateActionbar();

        beginRegenHealth();

        if (!debugMode) return;
        sender.sendMessageRaw("-------------------------");
        sender.sendMessageRaw(ChatColor.GOLD + "Damage Summary:");
        sender.sendDebugMessage("Raw damage dealt: " + ChatColor.RED + rawDamage);
        if (event.getDamage() != event.getFinalDamage()) this.sender.sendDebugMessage(ChatColor.RED + "Damage and raw damage do not match!");
        DecimalFormat format = new DecimalFormat("#.##");
        final String sDefense = format.format(DamageHelper.getDamageReduction(getAttribute(PlayerAttribute.DEFENSE)) * 100) + "% (" + getAttribute(PlayerAttribute.DEFENSE) + StatIcon.DEFENSE + ")";
        if (trueDamage) {
            sender.sendDebugMessage("Damage reduction (ignored, true damage): " + ChatColor.GREEN + sDefense);
        } else {
            sender.sendDebugMessage("Damage reduction: " + ChatColor.GREEN + sDefense);
        }
        sender.sendDebugMessage("Final damage: " + ChatColor.RED + damage);
        sender.sendDebugMessage("Damage to hearts: " + ChatColor.RED + (player.getHealth() - hearts));
        sender.sendDebugMessage("Final hearts: " + ChatColor.GREEN + hearts);
        sender.sendMessageRaw("-------------------------");
    }

    public void heal() {
        heal(getAttribute(PlayerAttribute.MAX_HEALTH));
    }
    public void heal(double amount) {
        health += amount;
        if (health >= getAttribute(PlayerAttribute.MAX_HEALTH)) {
            health = getAttribute(PlayerAttribute.MAX_HEALTH);
            stopRegen();
        }

        // Doing this instead of a simple LivingEntity#setHealth
        // because that method doesn't show the regen health animation
        CraftPlayer craftPlayer = (CraftPlayer) player;
        craftPlayer.setRealHealth(getHearts());
        craftPlayer.sendHealthUpdate();

        updateActionbar();
    }

    public boolean eat(double filling) {
        if (filling > hunger) return false;
        hunger -= filling;
        if (hunger < getAttribute(PlayerAttribute.MAX_HUNGER) && (getHungryRunnable == null || getHungryRunnable.isCancelled())) getHungryRunnable = new ScheduleBuilder().wait(4.0).seconds().repeatEvery(4.0).seconds().executes(runnable -> hungerTick(5)).run(plugin);
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
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
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
            plugin.getLogger().warning("Data file for player " + player.getName() + " (" + player.getUniqueId() + ") does not exist");
            return;
        }
        xp.setAmount(0);
        if (jsonObject.has("xp")) {
            xp.setAmount(jsonObject.get("xp").getAsLong());
        }

        if (jsonObject.has("amulet")) {
            JsonArray amuletSection = jsonObject.get("amulet").getAsJsonArray();
            for (JsonElement element : amuletSection) {
                JsonObject amuletItem = element.getAsJsonObject();
                int index = amuletItem.get("slot").getAsInt();
                Result.resolve(() -> PowerStone.valueOf(amuletItem.get("powerStone").getAsString()))
                        .ifOk(powerStone -> equipment.getAmulet().set(index, powerStone))
                        .ifError(none -> equipment.getAmulet().delete(index));
            }
        }
        plugin.getLogger().info("Loaded player data for " + player.getName() + " (" + player.getUniqueId() + ")");
    }

    public void saveData() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("xp", new JsonPrimitive(xp.getAmount()));
        Amulet amulet = equipment.getAmulet();
        JsonArray amuletArray = new JsonArray();
        for (int i = 0; i < amulet.getTotalSlots(); i++) {
            PowerStone powerStone = amulet.get(i);
            if (powerStone == null) continue;
            if (xp.getLevel() < powerStone.getData().getLevelRequirement()) continue;
            JsonObject amuletItem = new JsonObject();
            amuletItem.add("slot", new JsonPrimitive(i));
            amuletItem.add("powerStone", new JsonPrimitive(powerStone.name()));
            amuletArray.add(amuletItem);
        }
        jsonObject.add("amulet", amuletArray);
        try {
            FileWriter writer = new FileWriter(dataFile);
            gson.toJson(jsonObject, writer);
            writer.close();
            plugin.getLogger().info("Saved data for " + player.getName() + " (" + player.getUniqueId() + ")");
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save data for " + player.getName() + " (" + player.getUniqueId() + ")");
        }
    }

    public PlayerEquipment getEquipment() {
        return equipment;
    }

    public Experience getXp() {
        return xp;
    }

    public void addXp(long xp) {
        this.xp.setAmount(this.xp.getAmount() + xp);
        new Thread(this::saveData).start();
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
        return player;
    }

    public ArcadiaSender<Player> getSender() {
        return sender;
    }

    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    public boolean inDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    private void beginRegenHealth() {
        if (health < getAttribute(PlayerAttribute.MAX_HEALTH)) {
            if (regenHealthRunnable == null || regenHealthRunnable.isCancelled()) {
                regenHealthRunnable = ScheduleBuilder.create()
                        .wait(4.0).seconds()
                        .repeatEvery(4.0).seconds()
                        .executes(runnable -> heal(getAttribute(PlayerAttribute.MAX_HEALTH) / 10))
                        .run(plugin);
            }
        }
    }

    private void stopRegen() {
        if (regenHealthRunnable != null) regenHealthRunnable.cancel();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayerData that = (PlayerData) o;

        return player.getUniqueId().equals(that.player.getUniqueId());
    }

    private double getHearts() {
        org.bukkit.attribute.AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute == null) throw new IllegalStateException("unreachable");
        return health / getAttribute(PlayerAttribute.MAX_HEALTH) * attribute.getValue();
    }

    private double getMaxHearts() {
        return Math.floor(Math.min(Math.max(0.01 * getAttribute(PlayerAttribute.MAX_HEALTH) + 19.9, 20), 40) / 2) * 2;
    }

    private @Nullable File createDataFile() {
        File file = new File(plugin.getPlayerManager().getDataFolder().getPath(), player.getUniqueId() + ".json");
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

    public static @NotNull PlayerData create(@NotNull ArcadiaSender<Player> player, Arcadia plugin) {
        PlayerData stats = new PlayerData(player, plugin);
        stats.updateValues();
        return stats;
    }

    private static String formatDouble(double d, @NotNull DecimalFormat format) {
        return format.format(Math.ceil(d));
    }
}
