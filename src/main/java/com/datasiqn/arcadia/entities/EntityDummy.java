package com.datasiqn.arcadia.entities;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.loottable.LootTable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class EntityDummy implements ArcadiaEntitySummoner {
    private final String id;

    public EntityDummy(String id) {
        this.id = id;
    }

    @Override
    public void summonEntity(@NotNull Location location, Arcadia plugin) {
        if (location.getWorld() == null) return;
        new CustomEntity(plugin, location.getWorld(), id).summon(location);
    }

    public static class CustomEntity extends ArcadiaEntity {
        public CustomEntity(com.datasiqn.arcadia.Arcadia plugin, @NotNull World world, String id) {
            super(EntityType.ZOMBIE, plugin, world, "Dummy", id, Integer.MAX_VALUE);
            this.expToDrop = 100;
            AttributeInstance attribute = getAttribute(Attributes.KNOCKBACK_RESISTANCE);
            if (attribute != null) attribute.setBaseValue(1);
        }

        @Override
        protected void registerGoals() { }

        @Override
        protected @NotNull LootTable getArcadiaLootTable() {
            return LootTable.EMPTY;
        }
    }
}
