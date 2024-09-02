package com.datasiqn.arcadia.damage;

import com.datasiqn.arcadia.dungeon.DungeonPlayer;
import jline.internal.Nullable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class DamageCause {
    private final @Nullable DungeonPlayer source;
    private final @Nullable DamageSource damageSource;
    private final @Nullable DamageSource soundDamageSource;
    private final boolean direct;

    private DamageCause(@Nullable DungeonPlayer source, @Nullable DamageSource damageSource, @Nullable DamageSource soundDamageSource, boolean direct) {
        this.source = source;
        this.damageSource = damageSource;
        this.soundDamageSource = soundDamageSource;
        this.direct = direct;
    }

    public @Nullable DungeonPlayer getSource() {
        return source;
    }

    public @NotNull DamageSource getDamageSource(Entity entity) {
        if (source != null) return entity.damageSources().playerAttack(((CraftPlayer) source.getPlayer()).getHandle());
        if (damageSource != null) return damageSource;
        return entity.damageSources().generic();
    }

    public boolean hasSource() {
        return source != null;
    }

    public @NotNull DamageSource getSoundDamageSource(Entity entity) {
        if (soundDamageSource != null) return soundDamageSource;
        return getDamageSource(entity);
    }

    public boolean isDirect() {
        return direct;
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull DamageCause direct(DungeonPlayer player) {
        return direct(player, null);
    }
    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull DamageCause direct(DungeonPlayer player, @Nullable DamageSource soundDamageSource) {
        return new DamageCause(player, null, soundDamageSource, true);
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull DamageCause indirect(DungeonPlayer player) {
        return indirect(player, null);
    }
    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull DamageCause indirect(DungeonPlayer player, @Nullable DamageSource soundDamageSource) {
        return new DamageCause(player, null, soundDamageSource, false);
    }

    public static @NotNull DamageCause natural(DamageSource damageSource) {
        return natural(damageSource, damageSource);
    }
    public static @NotNull DamageCause natural(DamageSource damageSource, @Nullable DamageSource soundDamageSource) {
        return new DamageCause(null, damageSource, soundDamageSource, false);
    }
}
