package com.datasiqn.arcadia.enchants.modifiers;

import com.datasiqn.arcadia.entities.ArcadiaEntity;

import java.util.function.BiConsumer;

public class EntityEnchantModifier implements EnchantModifier {
    private final BiConsumer<ArcadiaEntity, Integer> modifyEntity;

    public EntityEnchantModifier(BiConsumer<ArcadiaEntity, Integer> modifyEntity) {
        this.modifyEntity = modifyEntity;
    }

    public void modifyEntity(ArcadiaEntity entity, int level) {
        modifyEntity.accept(entity, level);
    }
}
