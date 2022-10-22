package com.datasiqn.arcadia.entities;

import com.datasiqn.arcadia.loottables.LootTables;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class EntityZombie implements ArcadiaEntitySummoner {
    private final String id;

    public EntityZombie(String id) {
        this.id = id;
    }

    @Override
    public void summonEntity(@NotNull Location location) {
        if (location.getWorld() == null) return;
        new CustomEntity(location.getWorld(), id).summon(location);
    }

    private static class CustomEntity extends ArcadiaHostileEntity {
        public CustomEntity(@NotNull World world, String id) {
            super(EntityType.ZOMBIE, world, "Zombie", id, 10, 1.5);
            setItemInHand(InteractionHand.MAIN_HAND, new net.minecraft.world.item.ItemStack(Items.WOODEN_SWORD));
            setItemSlot(EquipmentSlot.HEAD, new net.minecraft.world.item.ItemStack(Items.LEATHER_HELMET));
        }

        @Override
        protected @NotNull LootTables getArcadiaLootTable() {
            return LootTables.ENTITY_ZOMBIE;
        }

        @Override
        protected @Nullable SoundEvent getHurtSound(DamageSource damagesource) {
            return SoundEvents.ZOMBIE_HURT;
        }

        @Override
        protected @Nullable SoundEvent getDeathSound() {
            return SoundEvents.ZOMBIE_DEATH;
        }

        @Override
        protected @Nullable SoundEvent getAmbientSound() {
            return SoundEvents.ZOMBIE_AMBIENT;
        }
    }
}
