package com.datasiqn.arcadia.datatype;

import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class ArcadiaDataType {
    private ArcadiaDataType() {}

    public static final PersistentDataType<PersistentDataContainer[], EnchantsDataType.EnchantData[]> ENCHANTS = new EnchantsDataType();
    public static final PersistentDataType<Byte, Boolean> BOOLEAN = new BooleanDataType();
}
