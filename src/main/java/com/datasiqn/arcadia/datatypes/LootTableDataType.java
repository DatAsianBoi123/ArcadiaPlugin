package com.datasiqn.arcadia.datatypes;

import com.datasiqn.arcadia.loottable.LootTables;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class LootTableDataType implements PersistentDataType<String, LootTables> {
    @NotNull
    @Override
    public Class<String> getPrimitiveType() {
        return String.class;
    }

    @NotNull
    @Override
    public Class<LootTables> getComplexType() {
        return LootTables.class;
    }

    @NotNull
    @Override
    public String toPrimitive(@NotNull LootTables complex, @NotNull PersistentDataAdapterContext context) {
        return complex.name();
    }

    @NotNull
    @Override
    public LootTables fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
        return LootTables.valueOf(primitive);
    }
}
