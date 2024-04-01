package com.datasiqn.arcadia.amulet;

import com.datasiqn.arcadia.item.material.data.ItemBuilder;
import com.datasiqn.arcadia.item.material.data.MaterialData;
import com.datasiqn.arcadia.item.type.ItemType;
import com.datasiqn.arcadia.item.type.data.NoneItemData;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class PowerStoneData extends MaterialData<NoneItemData> {
    private final int levelRequirement;

    public PowerStoneData(@NotNull Builder builder) {
        super(builder);

        this.levelRequirement = builder.levelRequirement;
    }

    @Override
    public @NotNull String getName() {
        if (super.getName() == null) throw new IllegalStateException("name is null");
        return super.getName();
    }

    public int getLevelRequirement() {
        return levelRequirement;
    }

    @Contract("_ -> new")
    public static @NotNull Builder builder(String name) {
        return new Builder(name);
    }

    public static class Builder extends ItemBuilder<NoneItemData, PowerStoneData, Builder> {
        private int levelRequirement = 0;

        public Builder(String name) {
            super(ItemType.POWER_STONE, null);
            name(name);

            stackable(false);
        }

        public Builder levelRequirement(int requirement) {
            this.levelRequirement = requirement;
            return this;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public @NotNull PowerStoneData build() {
            return new PowerStoneData(this);
        }
    }
}
