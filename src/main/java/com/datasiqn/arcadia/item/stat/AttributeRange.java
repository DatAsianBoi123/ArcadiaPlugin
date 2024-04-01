package com.datasiqn.arcadia.item.stat;

import java.text.DecimalFormat;

public record AttributeRange(double min, double max) {
    public boolean hasRange() {
        return min != max;
    }

    public double get(double itemQuality) {
        return min + (max - min) * itemQuality;
    }

    @Override
    public String toString() {
        DecimalFormat format = new DecimalFormat("#.###");
        return min == max ? format.format(max) : format.format(min) + "-" + format.format(max);
    }
}
