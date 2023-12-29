package com.datasiqn.arcadia.item.components;

import com.datasiqn.arcadia.entities.ArcadiaEntity;
import com.datasiqn.arcadia.item.ArcadiaItem;

public interface ItemComponent {
    default double modifyAttackDamage(ArcadiaEntity entity, double damage, ArcadiaItem item) {
        return damage;
    }
}
