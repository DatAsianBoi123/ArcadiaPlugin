package com.datasiqn.arcadia.datatypes;

import com.datasiqn.arcadia.enchants.EnchantType;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class EnchantIdDataType implements PersistentDataType<String, EnchantType> {
    @NotNull
    @Override
    public Class<String> getPrimitiveType() {
        return String.class;
    }

    @NotNull
    @Override
    public Class<EnchantType> getComplexType() {
        return EnchantType.class;
    }

    @NotNull
    @Override
    public String toPrimitive(@NotNull EnchantType complex, @NotNull PersistentDataAdapterContext context) {
        return complex.name();
    }

    @NotNull
    @Override
    public EnchantType fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
        return EnchantType.valueOf(primitive);
    }
}
