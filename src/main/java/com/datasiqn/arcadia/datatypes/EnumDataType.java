package com.datasiqn.arcadia.datatypes;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class EnumDataType<Z extends Enum<Z>> implements PersistentDataType<String, Z> {
    private final Class<Z> enumClass;
    private final ValueOfMethod<Z> valueOfMethod;

    public EnumDataType(@NotNull Class<Z> enumClass) {
        this.enumClass = enumClass;
        this.valueOfMethod = str -> Enum.valueOf(enumClass, str);
    }

    @NotNull
    @Override
    public Class<Z> getComplexType() {
        return enumClass;
    }

    @NotNull
    @Override
    public final Class<String> getPrimitiveType() {
        return String.class;
    }

    @NotNull
    @Override
    public String toPrimitive(@NotNull Z complex, @NotNull PersistentDataAdapterContext context) {
        return complex.name();
    }

    @NotNull
    @Override
    public Z fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
        return valueOfMethod.valueOf(primitive);
    }

    private interface ValueOfMethod<Z extends Enum<Z>> {
        Z valueOf(String str);
    }
}
