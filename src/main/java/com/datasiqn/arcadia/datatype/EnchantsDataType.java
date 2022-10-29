package com.datasiqn.arcadia.datatype;

import com.datasiqn.arcadia.ArcadiaKeys;
import com.datasiqn.arcadia.enchants.EnchantType;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnchantsDataType implements PersistentDataType<PersistentDataContainer[], EnchantsDataType.EnchantData[]> {
    @NotNull
    @Override
    public Class<PersistentDataContainer[]> getPrimitiveType() {
        return PersistentDataContainer[].class;
    }

    @NotNull
    @Override
    public Class<EnchantData[]> getComplexType() {
        return EnchantData[].class;
    }

    @Override
    public PersistentDataContainer @NotNull [] toPrimitive(EnchantData @NotNull [] complex, @NotNull PersistentDataAdapterContext context) {
        PersistentDataContainer[] enchantArray = new PersistentDataContainer[complex.length];
        for (int i = 0; i < complex.length; i++) {
            PersistentDataContainer pdc = context.newPersistentDataContainer();
            pdc.set(ArcadiaKeys.ENCHANT_ID, STRING, complex[i].enchantType.name());
            pdc.set(ArcadiaKeys.ENCHANT_LEVEL, INTEGER, complex[i].level);
            enchantArray[i] = pdc;
        }
        return enchantArray;
    }

    @Override
    public @Nullable EnchantData @NotNull [] fromPrimitive(PersistentDataContainer @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {
        EnchantData[] enchants = new EnchantData[primitive.length];
        for (int i = 0; i < enchants.length; i++) {
            PersistentDataContainer enchantData = primitive[i];
            if (!enchantData.has(ArcadiaKeys.ENCHANT_ID, STRING) || !enchantData.has(ArcadiaKeys.ENCHANT_LEVEL, INTEGER)) {
                continue;
            }
            try {
                enchants[i] = new EnchantData(EnchantType.valueOf(enchantData.get(ArcadiaKeys.ENCHANT_ID, STRING)), enchantData.getOrDefault(ArcadiaKeys.ENCHANT_LEVEL, INTEGER, 1));
            } catch (IllegalArgumentException e) {
                enchants[i] = null;
            }
        }
        return enchants;
    }

    public record EnchantData(EnchantType enchantType, int level) {}
}
