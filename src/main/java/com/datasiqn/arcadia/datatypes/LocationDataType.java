package com.datasiqn.arcadia.datatypes;

import com.datasiqn.arcadia.ArcadiaTag;
import com.datasiqn.arcadia.util.PdcUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class LocationDataType implements PersistentDataType<PersistentDataContainer, Location> {
    @NotNull
    @Override
    public Class<PersistentDataContainer> getPrimitiveType() {
        return PersistentDataContainer.class;
    }

    @NotNull
    @Override
    public Class<Location> getComplexType() {
        return Location.class;
    }

    @NotNull
    @Override
    public PersistentDataContainer toPrimitive(@NotNull Location complex, @NotNull PersistentDataAdapterContext context) {
        PersistentDataContainer container = context.newPersistentDataContainer();

        World world = complex.getWorld();
        if (world != null) {
            PdcUtil.set(container, ArcadiaTag.WORLD, world);
        }

        PdcUtil.set(container, ArcadiaTag.LOCATION_X, complex.getX());
        PdcUtil.set(container, ArcadiaTag.LOCATION_Y, complex.getY());
        PdcUtil.set(container, ArcadiaTag.LOCATION_Z, complex.getZ());

        return container;
    }

    @NotNull
    @Override
    public Location fromPrimitive(@NotNull PersistentDataContainer primitive, @NotNull PersistentDataAdapterContext context) {
        World world = PdcUtil.get(primitive, ArcadiaTag.WORLD);
        double x = PdcUtil.get(primitive, ArcadiaTag.LOCATION_X);
        double y = PdcUtil.get(primitive, ArcadiaTag.LOCATION_Y);
        double z = PdcUtil.get(primitive, ArcadiaTag.LOCATION_Z);

        return new Location(world, x, y, z);
    }
}
