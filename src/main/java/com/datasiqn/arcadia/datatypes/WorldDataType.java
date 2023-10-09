package com.datasiqn.arcadia.datatypes;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class WorldDataType implements PersistentDataType<String, World> {
    @NotNull
    @Override
    public Class<String> getPrimitiveType() {
        return String.class;
    }

    @NotNull
    @Override
    public Class<World> getComplexType() {
        return World.class;
    }

    @NotNull
    @Override
    public String toPrimitive(@NotNull World complex, @NotNull PersistentDataAdapterContext context) {
        return complex.getName();
    }

    @NotNull
    @Override
    public World fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
        World world = Bukkit.getWorld(primitive);
        if (world == null) throw new IllegalStateException("world " + primitive + " does not exist");
        return world;
    }
}
