package com.datasiqn.arcadia.commands.argument;

import com.datasiqn.arcadia.dungeon.DungeonInstance;
import com.datasiqn.arcadia.enchants.EnchantType;
import com.datasiqn.arcadia.entities.EntityType;
import com.datasiqn.arcadia.item.material.ArcadiaMaterial;
import com.datasiqn.arcadia.loottable.LootTable;
import com.datasiqn.arcadia.menu.MenuType;
import com.datasiqn.arcadia.player.PlayerData;
import com.datasiqn.arcadia.recipe.ArcadiaRecipe;
import com.datasiqn.arcadia.upgrade.UpgradeType;
import com.datasiqn.commandcore.argument.type.ArgumentType;

public final class ArcadiaArgumentType {
    private ArcadiaArgumentType() { }

    public static final ArgumentType<PlayerData> PLAYER = new PlayerArgumentType();

    public static final ArgumentType<ArcadiaMaterial> ITEM = new ArgumentType.EnumArgumentType<>(ArcadiaMaterial.class);

    public static final ArgumentType<EnchantType> ENCHANT = new ArgumentType.EnumArgumentType<>(EnchantType.class);

    public static final ArgumentType<LootTable> LOOT_TABLE = new ArgumentType.EnumArgumentType<>(LootTable.class);

    public static final ArgumentType<ArcadiaRecipe> RECIPE = new ArgumentType.EnumArgumentType<>(ArcadiaRecipe.class);

    public static final ArgumentType<UpgradeType> UPGRADE = new ArgumentType.EnumArgumentType<>(UpgradeType.class);

    public static final ArgumentType<EntityType> ENTITY = new ArgumentType.EnumArgumentType<>(EntityType.class);

    public static final ArgumentType<MenuType> GUI = new ArgumentType.EnumArgumentType<>(MenuType.class);

    public static final ArgumentType<DungeonInstance> DUNGEON = new DungeonArgumentType();
}
