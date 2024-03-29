package com.datasiqn.arcadia.entities;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.loottable.LootTable;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class EntityIronGiant implements ArcadiaEntitySummoner {
    private final String id;

    public EntityIronGiant(String id) {
        this.id = id;
    }

    @Override
    public void summonEntity(@NotNull Location location, Arcadia plugin) {
        if (location.getWorld() == null) return;
        new CustomEntity(plugin, location.getWorld(), id).summon(location);
    }

    private static class CustomEntity extends ArcadiaHostileEntity {
        private int attackAnimationTick;

        public CustomEntity(com.datasiqn.arcadia.Arcadia plugin, @NotNull World world, String id) {
            super(EntityType.IRON_GOLEM, plugin, world, "Iron Giant", id, 100000, 3000);
        }

        @Override
        public void aiStep() {
            super.aiStep();
            if (this.attackAnimationTick > 0) {
                this.attackAnimationTick--;
            }
        }

        @Override
        public boolean doHurtTarget(@NotNull Entity entity) {
            this.attackAnimationTick = 10;
            this.level.broadcastEntityEvent(this, (byte)4);
            boolean flag = entity.hurt(this.damageSources().mobAttack(this), 0);
            if (flag) {
                double d0;
                if (entity instanceof LivingEntity living) {
                    d0 = living.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
                } else {
                    d0 = 0.0;
                }

                double d2 = Math.max(0.0, 1.0 - d0);
                entity.setDeltaMovement(entity.getDeltaMovement().add(0.0, 0.4000000059604645 * d2, 0.0));
                this.doEnchantDamageEffects(this, entity);
            }

            this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
            return flag;
        }

        @Override
        protected void registerGoals() {
            goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8));
            goalSelector.addGoal(8, new RandomLookAroundGoal(this));
            goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.6));
            targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));

            registerAttackGoal();
        }

        @Override
        protected void registerAttackGoal() {
            goalSelector.addGoal(2, new CustomMeleeAttackGoal(this, 0.9, true, 0, 3));
        }

        @Override
        protected @NotNull LootTable getArcadiaLootTable() {
            return LootTable.ENTITY_IRON_GIANT;
        }

        @Override
        protected @Nullable SoundEvent getHurtSound(DamageSource damagesource) {
            return SoundEvents.IRON_GOLEM_HURT;
        }

        @Override
        protected @Nullable SoundEvent getDeathSound() {
            return SoundEvents.IRON_GOLEM_DEATH;
        }

        @Override
        protected void playStepSound(BlockPos blockPos, BlockState blockState) {
            playSound(SoundEvents.IRON_GOLEM_STEP, 1, 1);
        }
    }
}
