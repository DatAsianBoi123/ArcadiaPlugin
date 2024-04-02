package com.datasiqn.arcadia.item.stat;

import com.datasiqn.arcadia.player.AttributeFormat;
import org.jetbrains.annotations.NotNull;

public record AttributeRange(AttributeFormat format, double min, double max) {
    public static final double UNKNOWN_ITEM_QUALITY = -1;

    public boolean hasRange() {
        return min != max;
    }

    public double get(double itemQuality) {
        return min + (max - min) * itemQuality;
    }

    public @NotNull String getFormatted(double itemQuality) {
        if (itemQuality == UNKNOWN_ITEM_QUALITY) {
            if (!hasRange()) return format.format(min);
            return format.color() + AttributeFormat.FORMAT.format(min) + "-" + AttributeFormat.FORMAT.format(max) + format.icon();
        }
        return format.format(get(itemQuality));
    }
}
