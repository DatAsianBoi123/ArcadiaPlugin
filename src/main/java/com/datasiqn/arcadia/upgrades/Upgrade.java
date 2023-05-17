package com.datasiqn.arcadia.upgrades;

import com.datasiqn.arcadia.items.materials.data.MaterialData;
import com.datasiqn.arcadia.items.modifiers.LoreItemModifier;
import com.datasiqn.arcadia.items.type.ItemType;
import com.datasiqn.arcadia.items.type.data.NoneItemData;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Upgrade {
    private final UpgradeType type;

    private int amount;

    public Upgrade(UpgradeType type) {
        this(type, 1);
    }
    public Upgrade(UpgradeType type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    public UpgradeType getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public ItemStack toItemStack() {
        MaterialData<NoneItemData> materialData = new MaterialData.Builder<>(ItemType.NONE)
                .name(type.getDisplayName())
                .material(type.getMaterial())
                .rarity(type.getRarity())
                .addModifier(new LoreItemModifier(type.getDescription().toArray(String[]::new)))
                .build();
        return materialData.toItemStack(amount, UUID.randomUUID());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Upgrade upgrade = (Upgrade) o;

        return type == upgrade.type;
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }
}
