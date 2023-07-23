package com.datasiqn.arcadia.datatypes;

import com.datasiqn.arcadia.enchants.EnchantType;
import com.datasiqn.arcadia.item.ItemId;
import com.datasiqn.arcadia.loottable.LootTables;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public final class ArcadiaDataType {
    private ArcadiaDataType() {}

    public static final PersistentDataType<String, ItemId> ITEM_ID = new ItemIdDataType();

    public static final PersistentDataType<PersistentDataContainer[], EnchantsDataType.EnchantData[]> ENCHANTS = new EnchantsDataType();

    public static final PersistentDataType<String, EnchantType> ENCHANT_ID = new EnumDataType<>(EnchantType.class);

    public static final PersistentDataType<Byte, Boolean> BOOLEAN = new BooleanDataType();

    public static final PersistentDataType<String, UUID> UUID = new UuidDataType();

    public static final PersistentDataType<String, World> WORLD = new WorldDataType();

    public static final PersistentDataType<PersistentDataContainer, Location> LOCATION = new LocationDataType();

    public static final PersistentDataType<String, LootTables> LOOT_TABLE = new LootTableDataType();
}
