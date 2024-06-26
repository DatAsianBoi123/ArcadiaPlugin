package com.datasiqn.arcadia.commands.argument;

import com.datasiqn.arcadia.dungeon.DungeonInstance;
import com.datasiqn.arcadia.effect.ArcadiaEffectType;
import com.datasiqn.arcadia.entities.EntityType;
import com.datasiqn.arcadia.item.material.ArcadiaMaterial;
import com.datasiqn.arcadia.loottable.LootTable;
import com.datasiqn.arcadia.menu.MenuType;
import com.datasiqn.arcadia.npc.CreatedNpc;
import com.datasiqn.arcadia.player.PlayerAttribute;
import com.datasiqn.arcadia.player.PlayerData;
import com.datasiqn.arcadia.recipe.ArcadiaRecipe;
import com.datasiqn.arcadia.upgrade.UpgradeType;
import com.datasiqn.commandcore.argument.type.ArgumentType;
import com.datasiqn.commandcore.argument.type.EnumArgumentType;

public final class ArcadiaArgumentType {
    private ArcadiaArgumentType() { }

    public static final ArgumentType<PlayerData> PLAYER = new PlayerArgumentType();

    public static final ArgumentType<PlayerAttribute> PLAYER_ATTRIBUTE = new EnumArgumentType<>(PlayerAttribute.class, "attribute");

    public static final ArgumentType<ArcadiaMaterial> ITEM = new EnumArgumentType<>(ArcadiaMaterial.class, "item");

    public static final ArgumentType<LootTable> LOOT_TABLE = new EnumArgumentType<>(LootTable.class, "loot table");

    public static final ArgumentType<ArcadiaRecipe> RECIPE = new EnumArgumentType<>(ArcadiaRecipe.class, "recipe");

    public static final ArgumentType<UpgradeType> UPGRADE = new EnumArgumentType<>(UpgradeType.class, "upgrade");

    public static final ArgumentType<EntityType> ENTITY = new EnumArgumentType<>(EntityType.class, "entity");

    public static final ArgumentType<ArcadiaEffectType> EFFECT = new EnumArgumentType<>(ArcadiaEffectType.class, "effect");

    public static final ArgumentType<MenuType> GUI = new EnumArgumentType<>(MenuType.class, "menu");

    public static final ArgumentType<DungeonInstance> DUNGEON = new DungeonArgumentType();

    public static final ArgumentType<CreatedNpc> NPC = new NpcArgumentType();
}
