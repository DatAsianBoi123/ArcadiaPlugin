package com.datasiqn.arcadia;

import com.datasiqn.arcadia.datatypes.ArcadiaDataType;
import com.datasiqn.arcadia.datatypes.EnchantsDataType;
import com.datasiqn.arcadia.enchants.EnchantType;
import com.datasiqn.arcadia.item.ItemId;
import com.datasiqn.arcadia.loottable.LootTables;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public final class ArcadiaTag<T> {
    public static final ArcadiaTag<ItemId> ITEM_ID = new ArcadiaTag<>("id", ArcadiaDataType.ITEM_ID);
    public static final ArcadiaTag<UUID> ITEM_UUID = new ArcadiaTag<>("uuid", ArcadiaDataType.UUID);
    public static final ArcadiaTag<Boolean> ITEM_MATERIAL = new ArcadiaTag<>("material", ArcadiaDataType.BOOLEAN);
    public static final ArcadiaTag<Double> ITEM_QUALITY = new ArcadiaTag<>("quality", PersistentDataType.DOUBLE);

    public static final ArcadiaTag<EnchantsDataType.EnchantData[]> ITEM_ENCHANTS = new ArcadiaTag<>("enchants", ArcadiaDataType.ENCHANTS);
    public static final ArcadiaTag<EnchantType> ENCHANT_ID = new ArcadiaTag<>("id", ArcadiaDataType.ENCHANT_ID);
    public static final ArcadiaTag<Integer> ENCHANT_LEVEL = new ArcadiaTag<>("level", PersistentDataType.INTEGER);

    public static final ArcadiaTag<Double> ARROW_DAMAGE = new ArcadiaTag<>("arrow_damage", PersistentDataType.DOUBLE);

    public static final ArcadiaTag<Boolean> UPGRADE_BAG = new ArcadiaTag<>("upgrade_bag", ArcadiaDataType.BOOLEAN);

    public static final ArcadiaTag<Boolean> CRAFTING_RESULT = new ArcadiaTag<>("crafting", ArcadiaDataType.BOOLEAN);
    public static final ArcadiaTag<Boolean> ANVIL_RESULT = new ArcadiaTag<>("anvil_result", ArcadiaDataType.BOOLEAN);

    public static final ArcadiaTag<Boolean> UPGRADE_CHEST = new ArcadiaTag<>("upgrade_chest", ArcadiaDataType.BOOLEAN);
    public static final ArcadiaTag<Boolean> CHEST_OPENED = new ArcadiaTag<>("opened", ArcadiaDataType.BOOLEAN);
    public static final ArcadiaTag<Location> CHEST_LOC = new ArcadiaTag<>("chest_loc", ArcadiaDataType.LOCATION);

    public static final ArcadiaTag<LootTables> LOOT_TABLE = new ArcadiaTag<>("loot_chest", ArcadiaDataType.LOOT_TABLE);

    public static final ArcadiaTag<World> WORLD = new ArcadiaTag<>("world", ArcadiaDataType.WORLD);
    public static final ArcadiaTag<Double> LOCATION_X = new ArcadiaTag<>("x", PersistentDataType.DOUBLE);
    public static final ArcadiaTag<Double> LOCATION_Y = new ArcadiaTag<>("y", PersistentDataType.DOUBLE);
    public static final ArcadiaTag<Double> LOCATION_Z = new ArcadiaTag<>("z", PersistentDataType.DOUBLE);

    private final String key;
    private final PersistentDataType<?, T> dataType;

    private ArcadiaTag(String key, PersistentDataType<?, T> dataType) {
        this.key = key;
        this.dataType = dataType;
    }

    public String getKey() {
        return key;
    }

    public PersistentDataType<?, T> getDataType() {
        return dataType;
    }
}
