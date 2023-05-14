package com.datasiqn.arcadia.entities;

import com.datasiqn.arcadia.loottables.LootTables;
import com.datasiqn.arcadia.items.stats.StatIcon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.Random;

public abstract class ArcadiaEntity extends PathfinderMob {
    protected final Random rand = new Random();
    protected final World world;
    protected final String customName;
    protected final double maxHealth;
    private final String id;
    protected double health;

    private final Arcadia arcadia;

    public ArcadiaEntity(EntityType<? extends PathfinderMob> entityType, @NotNull World world, String name, String id, int maxHealth) {
        super(entityType, ((CraftWorld) world).getHandle());
        setHealth(1f);
        Objects.requireNonNull(getAttribute(Attributes.MAX_HEALTH)).setBaseValue(1d);
        setCustomNameVisible(true);

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

    public void damage(double amount, @NotNull EntityDamageEvent event) {
        event.setDamage(0);
        health = Mth.clamp(health - amount, 0, maxHealth);
        if (health <= 0) event.setDamage(getHealth() + 1);
        updateName();
    }

    public void summon(@NotNull Location location) {
        setPos(location.getX(), location.getY(), location.getZ());
        ((CraftWorld) world).addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    public Arcadia arcadia() {
        return arcadia;
    }

    protected final void updateName() {
        setCustomName(Component.literal(ChatColor.GREEN + customName + " " + ChatColor.RED + formatDouble(health) + ChatColor.DARK_GRAY + "/" + ChatColor.RED + formatDouble(maxHealth) + StatIcon.HEALTH));
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
    protected abstract LootTables getArcadiaLootTable();

    private String formatDouble(double d) {
        DecimalFormat format = new DecimalFormat("#,###");
        return format.format(Math.ceil(d));
    }

    public record Arcadia(String id, double health, double maxHealth) { }
}
