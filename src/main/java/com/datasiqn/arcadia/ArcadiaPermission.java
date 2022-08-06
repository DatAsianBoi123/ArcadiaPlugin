package com.datasiqn.arcadia;

import org.bukkit.ChatColor;

public abstract class ArcadiaPermission {
    public static final String PERMISSION_USE_HEAL = "arcadia.heal";
    public static final String PERMISSION_USE_ITEM = "arcadia.item";
    public static final String PERMISSION_USE_SUMMON = "arcadia.summon";
    public static final String PERMISSION_USE_GUI = "arcadia.gui";
    public static final String PERMISSION_USE_HELP = "arcadia.help";
    public static final String PERMISSION_USE_DEBUG = "arcadia.debug";
    public static final String PERMISSION_USE_RECIPE = "arcadia.recipe";
    public static final String PERMISSION_USE_LOOT = "arcadia.loot";
    public static final String PERMISSION_USE_ENCHANT = "arcadia.enchant";

    public static final String MISSING_PERMISSIONS = ChatColor.RED + "You do not have permission to use this command.";
}
