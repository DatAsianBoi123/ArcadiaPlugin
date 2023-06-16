package com.datasiqn.arcadia.commands.argument;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.dungeon.DungeonInstance;
import com.datasiqn.arcadia.enchants.EnchantType;
import com.datasiqn.arcadia.entities.EntityType;
import com.datasiqn.arcadia.item.material.ArcadiaMaterial;
import com.datasiqn.arcadia.loottable.LootTables;
import com.datasiqn.arcadia.menu.MenuType;
import com.datasiqn.arcadia.recipe.ArcadiaRecipe;
import com.datasiqn.arcadia.upgrade.UpgradeType;
import com.datasiqn.commandcore.argument.ArgumentType;
import com.datasiqn.resultapi.Result;

public interface ArcadiaArgumentType {
    ArgumentType<ArcadiaMaterial> ITEM = new ArgumentType.EnumArgumentType<>(ArcadiaMaterial.class);

    ArgumentType<EnchantType> ENCHANT = new ArgumentType.EnumArgumentType<>(EnchantType.class);

    ArgumentType<LootTables> LOOT_TABLE = new ArgumentType.EnumArgumentType<>(LootTables.class);

    ArgumentType<ArcadiaRecipe> RECIPE = new ArgumentType.EnumArgumentType<>(ArcadiaRecipe.class);

    ArgumentType<UpgradeType> UPGRADE = new ArgumentType.EnumArgumentType<>(UpgradeType.class);

    ArgumentType<EntityType> ENTITY = new ArgumentType.EnumArgumentType<>(EntityType.class);

    ArgumentType<MenuType> GUI = new ArgumentType.EnumArgumentType<>(MenuType.class);

    ArgumentType<DungeonInstance> DUNGEON = new ArgumentType.CustomArgumentType<>(reader -> Result.<String, String>ok(reader.nextWord())
            .andThen(str -> Result.ofNullable(Arcadia.getPlugin(Arcadia.class).getDungeonManager().getCreatedDungeon(str), "The dungeon '" + str + "' does not exist.")), context -> Arcadia.getPlugin(Arcadia.class).getDungeonManager().getAllDungeonInstances().stream().map(DungeonInstance::getId).toList());
}
