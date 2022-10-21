package com.datasiqn.arcadia;

import org.bukkit.NamespacedKey;

public final class ArcadiaKeys {
    private ArcadiaKeys() {}

    /**
     * string
     */
    public static final NamespacedKey ITEM_ID = Arcadia.getNK("id");
    /**
     * string
     */
    public static final NamespacedKey ITEM_UUID = Arcadia.getNK("uuid");
    /**
     * byte
     */
    public static final NamespacedKey ITEM_MATERIAL = Arcadia.getNK("material");
    /**
     * double
     */
    public static final NamespacedKey ITEM_QUALITY_BONUS = Arcadia.getNK("quality_bonus");

    /**
     * EnchantsDataType
     */
    public static final NamespacedKey ITEM_ENCHANTS = Arcadia.getNK("enchants");
    /**
     * string
     */
    public static final NamespacedKey ENCHANT_ID = Arcadia.getNK("id");
    /**
     * int
     */
    public static final NamespacedKey ENCHANT_LEVEL = Arcadia.getNK("level");
    /**
     * double
     */
    public static final NamespacedKey ARROW_DAMAGE = Arcadia.getNK("arrow_damage");

    /**
     * byte
     */
    public static final NamespacedKey CRAFTING_RESULT = Arcadia.getNK("crafting");
    /**
     * byte
     */
    public static final NamespacedKey ANVIL_RESULT = Arcadia.getNK("anvil_result");

    /**
     * byte
     */
    public static final NamespacedKey UPGRADE_CHEST = Arcadia.getNK("upgrade_chest");
    /**
     * byte
     */
    public static final NamespacedKey CHEST_OPENED = Arcadia.getNK("opened");
    /**
     * int[]
     */
    public static final NamespacedKey CHEST_LOC = Arcadia.getNK("chest_loc");

    public static final NamespacedKey LOOT_CHEST_LOOT_TABLE = Arcadia.getNK("loot_chest");
}
