package com.datasiqn.arcadia.datatypes;

import com.datasiqn.arcadia.ArcadiaTag;
import com.datasiqn.arcadia.enchants.EnchantType;
import com.datasiqn.arcadia.util.PdcUtil;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

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
            PdcUtil.set(pdc, ArcadiaTag.ENCHANT_ID, complex[i].enchantType);
            PdcUtil.set(pdc, ArcadiaTag.ENCHANT_LEVEL, complex[i].level);
            enchantArray[i] = pdc;
        }
        return enchantArray;
    }

    @Override
    public @NotNull EnchantData @NotNull [] fromPrimitive(PersistentDataContainer @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {
        EnchantData[] enchants = new EnchantData[primitive.length];
        for (int i = 0; i < enchants.length; i++) {
            PersistentDataContainer enchantData = primitive[i];
            enchants[i] = new EnchantData(PdcUtil.get(enchantData, ArcadiaTag.ENCHANT_ID), PdcUtil.get(enchantData, ArcadiaTag.ENCHANT_LEVEL));
        }
        return enchants;
    }

    public record EnchantData(EnchantType enchantType, int level) {}
}
