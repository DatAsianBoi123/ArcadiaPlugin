package com.datasiqn.arcadia.items.stats;

import java.text.DecimalFormat;

public record AttributeRange(double min, double max) {
    public boolean hasRange() {
        return min != max;
    }

    @Override
    public String toString() {
        DecimalFormat format = new DecimalFormat("#.###");
        return min == max ? format.format(max) : format.format(min) + "-" + format.format(max);
    }
}
