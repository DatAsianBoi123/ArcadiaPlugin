package com.datasiqn.arcadia.datatype;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class UuidDataType implements PersistentDataType<String, UUID> {
    @NotNull
    @Override
    public Class<String> getPrimitiveType() {
        return String.class;
    }

    @NotNull
    @Override
    public Class<UUID> getComplexType() {
        return UUID.class;
    }

    @NotNull
    @Override
    public String toPrimitive(@NotNull UUID complex, @NotNull PersistentDataAdapterContext context) {
        return complex.toString();
    }

    @NotNull
    @Override
    public UUID fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
        return UUID.fromString(primitive);
    }
}
