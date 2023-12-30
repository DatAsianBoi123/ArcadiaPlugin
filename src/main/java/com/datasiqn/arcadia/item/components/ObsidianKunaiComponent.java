package com.datasiqn.arcadia.item.components;

import com.datasiqn.arcadia.entities.ArcadiaEntity;
import com.datasiqn.arcadia.item.ArcadiaItem;
import org.jetbrains.annotations.NotNull;

public class ObsidianKunaiComponent implements ItemComponent {
    @Override
    public double modifyAttackDamage(@NotNull ArcadiaEntity entity, double damage, @NotNull ArcadiaItem item) {
        if (entity.isMarked("mark-ability:marked")) return damage * 2;
        return damage;
    }
}
