package com.datasiqn.arcadia.amulet;

import com.datasiqn.arcadia.item.material.data.ItemBuilder;
import com.datasiqn.arcadia.item.material.data.MaterialData;
import com.datasiqn.arcadia.item.type.ItemType;
import com.datasiqn.arcadia.item.type.data.NoneItemData;
import com.datasiqn.arcadia.player.PlayerAttribute;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.Map;

public class PowerStoneData extends MaterialData<NoneItemData> {
    private final Object2DoubleMap<PlayerAttribute> attributeMap;
    private final int levelRequirement;

    public PowerStoneData(@NotNull Builder builder) {
        super(builder);

        this.attributeMap = builder.attributeMap;
        this.levelRequirement = builder.levelRequirement;
    }

    @Override
    public @NotNull String getName() {
        if (super.getName() == null) throw new IllegalStateException("name is null");
        return super.getName();
    }

    public double getAttribute(PlayerAttribute attribute) {
        return attributeMap.getOrDefault(attribute, 0);
    }

    @UnmodifiableView
    public Map<PlayerAttribute, Double> getAttributes() {
        return Collections.unmodifiableMap(attributeMap);
    }

    public int getLevelRequirement() {
        return levelRequirement;
    }

    @Contract("_ -> new")
    public static @NotNull Builder builder(String name) {
        return new Builder(name);
    }

    public static class Builder extends ItemBuilder<NoneItemData, PowerStoneData, Builder> {
        private final Object2DoubleMap<PlayerAttribute> attributeMap = new Object2DoubleOpenHashMap<>();

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

        public Builder addAttribute(PlayerAttribute attribute, double amount) {
            attributeMap.put(attribute, amount);
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
