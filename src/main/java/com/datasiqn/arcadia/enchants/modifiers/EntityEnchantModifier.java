package com.datasiqn.arcadia.enchants.modifiers;

import com.datasiqn.arcadia.entities.ArcadiaMinecraftEntity;

import java.util.function.BiConsumer;

public class EntityEnchantModifier implements EnchantModifier {
    private final BiConsumer<ArcadiaMinecraftEntity, Integer> modifyEntity;

    public EntityEnchantModifier(BiConsumer<ArcadiaMinecraftEntity, Integer> modifyEntity) {
        this.modifyEntity = modifyEntity;
    }

    public void modifyEntity(ArcadiaMinecraftEntity entity, int level) {
        modifyEntity.accept(entity, level);
    }
}
