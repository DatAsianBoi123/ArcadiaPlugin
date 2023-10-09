package com.datasiqn.arcadia.entities;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.loottable.LootTable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class EntityUndeadGuardian implements ArcadiaEntitySummoner {
    private final String id;

    public EntityUndeadGuardian(String id) {
        this.id = id;
    }

    @Override
    public void summonEntity(@NotNull Location location, Arcadia plugin) {
        if (location.getWorld() == null) return;
        new CustomEntity(plugin, location.getWorld(), id).summon(location);
    }

    private static class CustomEntity extends ArcadiaHostileEntity {
        public CustomEntity(com.datasiqn.arcadia.Arcadia plugin, World world, String id) {
            super(EntityType.SKELETON, plugin, world, "Undead Guardian", id, 200, 250);

            AttributeInstance moveSpeed = getAttribute(Attributes.MOVEMENT_SPEED);
            if (moveSpeed != null) moveSpeed.setBaseValue(0.4);
            AttributeInstance knockbackResistance = getAttribute(Attributes.KNOCKBACK_RESISTANCE);
            if (knockbackResistance != null) knockbackResistance.setBaseValue(1);
        }

        @Override
        protected void registerAttackGoal() {
            goalSelector.addGoal(2, new CustomMeleeAttackGoal(this, 1, true, 5, 3));
        }

        @Override
        protected @NotNull LootTable getArcadiaLootTable() {
            return LootTable.ENTITY_UNDEAD_GUARDIAN;
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
