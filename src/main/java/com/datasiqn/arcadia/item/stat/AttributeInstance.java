package com.datasiqn.arcadia.item.stat;

import org.jetbrains.annotations.NotNull;

public class AttributeInstance {
    private final AttributeRange range;
    private double itemQuality;

    public AttributeInstance(double value) {
        this.range = new AttributeRange(value, value);
    }
    public AttributeInstance(@NotNull AttributeRange range) {
        this.range = range;
    }

    public @NotNull AttributeRange getRange() {
        return range;
    }

    public boolean isRandom() {
        return range.hasRange();
    }

    public double getValue() {
        return range.min() + (range.max() - range.min()) * itemQuality;
    }

    public void setItemQuality(double itemQuality) {
        this.itemQuality = itemQuality;
    }
}
