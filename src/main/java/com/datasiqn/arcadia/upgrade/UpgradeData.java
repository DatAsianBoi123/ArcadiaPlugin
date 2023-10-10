package com.datasiqn.arcadia.upgrade;

import com.datasiqn.arcadia.item.material.data.ItemBuilder;
import com.datasiqn.arcadia.item.material.data.MaterialData;
import com.datasiqn.arcadia.item.type.ItemType;
import com.datasiqn.arcadia.item.type.data.NoneItemData;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class UpgradeData extends MaterialData<NoneItemData> {
    public UpgradeData(@NotNull Builder builder) {
        super(builder);
    }

    @Contract(" -> new")
    public static @NotNull Builder builder() {
        return new Builder();
    }

    public static class Builder extends ItemBuilder<NoneItemData, UpgradeData, Builder> {
        public Builder() {
            super(ItemType.NONE, null);
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public @NotNull UpgradeData build() {
            return new UpgradeData(this);
        }
    }
}
