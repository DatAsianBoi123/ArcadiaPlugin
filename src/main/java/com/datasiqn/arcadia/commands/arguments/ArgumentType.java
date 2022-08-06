package com.datasiqn.arcadia.commands.arguments;

import com.datasiqn.arcadia.enchants.EnchantType;
import com.datasiqn.arcadia.entities.EntityType;
import com.datasiqn.arcadia.entities.loottables.LootTables;
import com.datasiqn.arcadia.guis.GUIType;
import com.datasiqn.arcadia.items.types.ArcadiaMaterial;
import com.datasiqn.arcadia.recipes.ArcadiaRecipe;
import com.datasiqn.arcadia.util.ParseUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface ArgumentType<T> {
    ArgumentType<String> STRING = new StringArgumentType();
    ArgumentType<Integer> INTEGER = new CustomArgumentType<>(ParseUtil::parseInt);
    ArgumentType<Boolean> BOOLEAN = new CustomArgumentType<>(ParseUtil::parseBoolean, List.of("true", "false"));
    ArgumentType<ArcadiaMaterial> ITEM = new EnumArgumentType<>(ArcadiaMaterial.class);
    ArgumentType<GUIType> GUI = new EnumArgumentType<>(GUIType.class);
    ArgumentType<EntityType> ENTITY = new EnumArgumentType<>(EntityType.class);
    ArgumentType<ArcadiaRecipe> RECIPE = new EnumArgumentType<>(ArcadiaRecipe.class);
    ArgumentType<LootTables> LOOT_TABLE = new EnumArgumentType<>(LootTables.class);
    ArgumentType<EnchantType> ENCHANT = new EnumArgumentType<>(EnchantType.class);

    @NotNull
    Optional<T> fromString(@NotNull String str);

    @NotNull
    default List<String> all() {
        return new ArrayList<>();
    }

    class EnumArgumentType<T extends Enum<T>> implements ArgumentType<T> {
        private final Class<T> enumClass;

        public EnumArgumentType(Class<T> enumClass) {
            this.enumClass = enumClass;
        }

        @Override
        public @NotNull Optional<T> fromString(@NotNull String str) {
            try {
                return Optional.of(T.valueOf(enumClass, str.toUpperCase()));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        }

        @Override
        public @NotNull List<String> all() {
            return Arrays.stream(enumClass.getEnumConstants()).map(t -> t.name().toLowerCase(Locale.ROOT)).collect(Collectors.toList());
        }
    }

    class CustomArgumentType<T> implements ArgumentType<T> {
        private final Function<String, T> asStringFunction;
        private final List<String> values;

        public CustomArgumentType(Function<String, T> asStringFunction) {
            this(asStringFunction, Collections.emptyList());
        }
        public CustomArgumentType(Function<String, T> asStringFunction, List<String> values) {
            this.asStringFunction = asStringFunction;
            this.values = values;
        }

        @Override
        public @NotNull Optional<T> fromString(@NotNull String str) {
            return Optional.ofNullable(asStringFunction.apply(str));
        }

        @Override
        public @NotNull List<String> all() {
            return values;
        }
    }

    class StringArgumentType implements ArgumentType<String> {

        @Override
        public @NotNull Optional<String> fromString(@NotNull String str) {
            return Optional.of(str);
        }
    }
}
