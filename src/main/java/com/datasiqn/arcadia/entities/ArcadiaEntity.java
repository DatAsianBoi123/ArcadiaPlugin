package com.datasiqn.arcadia.entities;

import com.datasiqn.arcadia.damage.DamageCause;
import com.datasiqn.arcadia.effect.ActiveEffect;
import com.datasiqn.arcadia.effect.ArcadiaEffect;
import com.datasiqn.arcadia.effect.ArcadiaEffectType;
import com.datasiqn.arcadia.loottable.LootTable;
import com.datasiqn.arcadia.player.AttributeFormats;
import com.datasiqn.arcadia.player.PlayerData;
import com.datasiqn.arcadia.upgrade.actions.DamageEnemyAction;
import com.datasiqn.arcadia.upgrade.actions.KillEnemyAction;
import com.datasiqn.commandcore.argument.duration.Duration;
import com.datasiqn.schedulebuilder.ScheduleBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundDamageEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public abstract class ArcadiaEntity extends PathfinderMob {
    protected final Random rand = new Random();
    protected final com.datasiqn.arcadia.Arcadia plugin;
    protected final LinkedHashMap<ArcadiaEffectType, ActiveEffect> activeArcadiaEffects = new LinkedHashMap<>();
    protected final Set<String> marks = new HashSet<>();
    protected final World world;
    protected final String customName;
    protected final double maxHealth;
    protected final String id;
    protected double health;

    private final Arcadia arcadia;

    public ArcadiaEntity(EntityType<? extends PathfinderMob> entityType, com.datasiqn.arcadia.Arcadia plugin, @NotNull World world, String name, String id, int maxHealth) {
        super(entityType, ((CraftWorld) world).getHandle());
        setHealth(1f);
        AttributeInstance attribute = getAttribute(Attributes.MAX_HEALTH);
        if (attribute != null) attribute.setBaseValue(1d);
        setCustomNameVisible(true);

        this.plugin = plugin;
        this.id = id;
        this.world = world;
        this.customName = name;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.arcadia = new Arcadia(id, health, maxHealth);

        updateName();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        CompoundTag innerTag = new CompoundTag();
        innerTag.putDouble("hp", health);
        innerTag.putString("id", id);
        tag.put("ArcadiaData", innerTag);
    }

    public void handleNaturalDamageEvent(@NotNull EntityDamageEvent event) {
        double damage = event.getDamage();
        event.setDamage(0);
        DamageSource lastDamageSource = getLastDamageSource();
        handleDamage(damage, () -> event.setDamage(getHealth() + 1), DamageCause.natural(lastDamageSource), true);
    }

    public void handleDamageEvent(@NotNull EntityDamageEvent event, @NotNull DamageCause player) {
        double damage = event.getDamage();
        event.setDamage(0);
        handleDamage(damage, () -> event.setDamage(getHealth() + 1), player, true);
    }

    public void damage(double amount, @NotNull DamageCause cause, boolean emitDamageEvent) {
        if (health <= 0) return;
        DamageSource damageSource = cause.getDamageSource(this);
        ((ServerLevel) level).getChunkSource().broadcastAndSend(this, new ClientboundDamageEventPacket(this, damageSource));
        playHurtSound(cause.getSoundDamageSource(this));
        handleDamage(amount, () -> {
            setHealth(0);
            die(damageSource);
        }, cause, emitDamageEvent);
    }

    public void summon(@NotNull Location location) {
        setPos(location.getX(), location.getY(), location.getZ());
        ((CraftWorld) world).addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    public void mark(String mark) {
        marks.add(mark);
    }

    public void unmark(String mark) {
        marks.remove(mark);
    }

    public boolean isMarked(String mark) {
        return marks.contains(mark);
    }

    public void addArcadiaEffect(ArcadiaEffectType type, Duration duration, @Nullable PlayerData effector) {
        activeArcadiaEffects.compute(type, (effectType, activeEffect) -> {
            if (activeEffect == null) return type.bind(this, effector, duration, plugin);
            activeEffect.addStack();
            return activeEffect;
        });
        updateName();
    }

    public void removeArcadiaEffect(ArcadiaEffectType type) {
        ActiveEffect effect = activeArcadiaEffects.remove(type);
        if (effect == null) return;
        effect.end();
        updateName();
    }

    public void clearArcadiaEffects() {
        for (ActiveEffect effect : activeArcadiaEffects.values()) effect.end();
        activeArcadiaEffects.clear();
        updateName();
    }

    public Arcadia arcadia() {
        return arcadia;
    }

    protected final void updateName() {
        MutableComponent effectsComponent = activeArcadiaEffects.values().stream()
                .map(effect -> {
                    ArcadiaEffect arcadiaEffect = effect.getEffect();
                    String stacks = effect.stacks() == 0 ? String.valueOf(effect.stacks()) : "";
                    return Component.literal(arcadiaEffect.getColor() + stacks + arcadiaEffect.getIcon());
                })
                .reduce(Component.empty(), (left, right) -> left.append(ChatColor.RESET.toString()).append(right).append(" " + ChatColor.RESET));
        setCustomName(effectsComponent.append(ChatColor.RESET + "" + ChatColor.GREEN + customName + " " + ChatColor.RED + formatDouble(health) + ChatColor.DARK_GRAY + "/" + ChatColor.RED + formatDouble(maxHealth) + AttributeFormats.HEALTH.icon()));
    }

    @Override
    public void tick() {
        super.tick();

        if (activeArcadiaEffects.values().removeIf(ActiveEffect::tick)) updateName();
    }

    @Override
    protected void dropFromLootTable(@NotNull DamageSource damagesource, boolean flag) {
        if (!(damagesource.getEntity() instanceof Player)) return;
        getArcadiaLootTable().getLootTable().generateItems(rand).forEach(item -> spawnAtLocation(CraftItemStack.asNMSCopy(item.build())));
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource randomsource, DifficultyInstance difficultyInstance) { }

    @Override
    protected float getEquipmentDropChance(EquipmentSlot equipmentSlot) {
        return 0;
    }

    @Override
    public boolean removeWhenFarAway(double d0) {
        return false;
    }

    @Override
    protected abstract void registerGoals();

    @NotNull
    protected abstract LootTable getArcadiaLootTable();

    private String formatDouble(double d) {
        DecimalFormat format = new DecimalFormat("#,###");
        return format.format(Math.ceil(d));
    }

    private void handleDamage(double damage, Runnable deathRunnable, @NotNull DamageCause cause, boolean emitDamageEvent) {
        DamageEnemyAction action = new DamageEnemyAction(cause, this, damage, plugin);
        if (cause.hasSource() && emitDamageEvent) plugin.getUpgradeEventManager().emit(action);
        double finalDamage = action.getDamage();
        health = Mth.clamp(health - finalDamage, 0, maxHealth);
        if (health <= 0) {
            deathRunnable.run();
            if (cause.hasSource()) plugin.getUpgradeEventManager().emit(new KillEnemyAction(cause.getSource(), this, plugin));
        }
        updateName();
        spawnDamageIndicator(finalDamage);
    }

    private void spawnDamageIndicator(double damage) {
        Vector direction = new Vector(Math.random() - 0.5, 0, Math.random() - 0.5);
        direction.normalize();
        direction.setY(1);
        Location location = getBukkitEntity().getLocation();
        location.add(direction);
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.ENGLISH);
        TextDisplay entity = world.spawn(location, TextDisplay.class, display -> {
            display.setText(ChatColor.RED + numberFormat.format(Math.round(damage)));
            display.setAlignment(TextDisplay.TextAlignment.CENTER);
            display.setBillboard(Display.Billboard.CENTER);
        });
        ScheduleBuilder.create()
                .wait(1.0).seconds()
                .executes(runnable -> entity.remove())
                .run(plugin);
    }

    public record Arcadia(String id, double health, double maxHealth) { }
}
