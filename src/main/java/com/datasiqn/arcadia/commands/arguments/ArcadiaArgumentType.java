package com.datasiqn.arcadia.commands.arguments;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.dungeons.DungeonInstance;
import com.datasiqn.arcadia.enchants.EnchantType;
import com.datasiqn.arcadia.entities.EntityType;
import com.datasiqn.arcadia.loottables.LootTables;
import com.datasiqn.arcadia.guis.GUIType;
import com.datasiqn.arcadia.items.materials.ArcadiaMaterial;
import com.datasiqn.arcadia.recipes.ArcadiaRecipe;
import com.datasiqn.arcadia.upgrades.UpgradeType;
import com.datasiqn.commandcore.arguments.ArgumentType;
import com.datasiqn.resultapi.Result;

public interface ArcadiaArgumentType {
    ArgumentType<ArcadiaMaterial> ITEM = new ArgumentType.EnumArgumentType<>(ArcadiaMaterial.class);

    ArgumentType<EnchantType> ENCHANT = new ArgumentType.EnumArgumentType<>(EnchantType.class);

    ArgumentType<LootTables> LOOT_TABLE = new ArgumentType.EnumArgumentType<>(LootTables.class);

    ArgumentType<ArcadiaRecipe> RECIPE = new ArgumentType.EnumArgumentType<>(ArcadiaRecipe.class);

    ArgumentType<UpgradeType> UPGRADE = new ArgumentType.EnumArgumentType<>(UpgradeType.class);

    ArgumentType<EntityType> ENTITY = new ArgumentType.EnumArgumentType<>(EntityType.class);

    ArgumentType<GUIType> GUI = new ArgumentType.EnumArgumentType<>(GUIType.class);

    ArgumentType<DungeonInstance> DUNGEON = new ArgumentType.CustomArgumentType<>(reader -> Result.<String, String>ok(reader.nextWord())
            .andThen(str -> Result.ofNullable(Arcadia.getPlugin(Arcadia.class).getDungeonManager().getCreatedDungeon(str), "The dungeon '" + str + "' does not exist.")), context -> Arcadia.getPlugin(Arcadia.class).getDungeonManager().getAllDungeonInstances().stream().map(DungeonInstance::getId).toList());
}
