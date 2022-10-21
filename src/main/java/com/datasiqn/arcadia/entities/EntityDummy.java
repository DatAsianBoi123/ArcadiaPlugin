package com.datasiqn.arcadia.entities;

import com.datasiqn.arcadia.entities.loottables.LootTables;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EntityDummy implements ArcadiaEntitySummoner {
    private final String id;

    public EntityDummy(String id) {
        this.id = id;
    }

    @Override
    public void summonEntity(@NotNull Location location) {
        if (location.getWorld() == null) return;
        new CustomEntity(location.getWorld(), id).summon(location);
    }

    public static class CustomEntity extends ArcadiaEntity {
        public CustomEntity(@NotNull World world, String id) {
            super(EntityType.ZOMBIE, world, "Dummy", id, 1_000_000);
            Objects.requireNonNull(getAttribute(Attributes.KNOCKBACK_RESISTANCE)).setBaseValue(1);
        }

        @Override
        protected void registerGoals() { }

        @Override
        public void dropExperience() {
            expToDrop = 100;
            super.dropExperience();
        }

        @Override
        protected @NotNull LootTables getArcadiaLootTable() {
            return LootTables.EMPTY;
        }
    }
}
