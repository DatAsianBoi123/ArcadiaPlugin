package com.datasiqn.arcadia;

import org.bukkit.NamespacedKey;

public abstract class ArcadiaKeys {
    public static final NamespacedKey ITEM_ID = Arcadia.getNK("id");
    public static final NamespacedKey ITEM_UUID = Arcadia.getNK("uuid");
    public static final NamespacedKey ITEM_MATERIAL = Arcadia.getNK("material");
    public static final NamespacedKey ITEM_QUALITY_BONUS = Arcadia.getNK("quality_bonus");

    public static final NamespacedKey ITEM_ENCHANTS = Arcadia.getNK("enchants");
    public static final NamespacedKey ENCHANT_ID = Arcadia.getNK("id");
    public static final NamespacedKey ENCHANT_LEVEL = Arcadia.getNK("level");

    public static final NamespacedKey ARROW_DAMAGE = Arcadia.getNK("arrow_damage");

    public static final NamespacedKey CRAFTING_RESULT = Arcadia.getNK("crafting");
    public static final NamespacedKey ANVIL_RESULT = Arcadia.getNK("anvil_result");
}
