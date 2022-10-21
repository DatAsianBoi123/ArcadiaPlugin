package com.datasiqn.arcadia.entities;

import com.datasiqn.arcadia.entities.loottables.LootTables;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;

public class EntityUndeadGuardian implements ArcadiaEntitySummoner {
    private final String id;

    public EntityUndeadGuardian(String id) {
        this.id = id;
    }

    @Override
    public void summonEntity(@NotNull Location location) {
        if (location.getWorld() == null) return;
        new CustomEntity(location.getWorld(), id).summon(location);
    }

    private static class CustomEntity extends ArcadiaHostileEntity {
        public CustomEntity(World world, String id) {
            super(EntityType.SKELETON, world, "Undead Guardian", id, 200, 250);

            Objects.requireNonNull(getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(0.4);
            Objects.requireNonNull(getAttribute(Attributes.KNOCKBACK_RESISTANCE)).setBaseValue(1);
        }

        @Override
        protected void registerAttackGoal() {
            goalSelector.addGoal(2, new CustomMeleeAttackGoal(this, 1, true, 5, 3));
        }

        @Override
        protected @NotNull LootTables getArcadiaLootTable() {
            return LootTables.UNDEAD_GUARDIAN;
        }

        @Override
        protected @Nullable SoundEvent getHurtSound(DamageSource damagesource) {
            return SoundEvents.SKELETON_HURT;
        }

        @Override
        protected @Nullable SoundEvent getDeathSound() {
            return SoundEvents.SKELETON_DEATH;
        }

        @Override
        protected @Nullable SoundEvent getAmbientSound() {
            return SoundEvents.SKELETON_AMBIENT;
        }
    }
}
