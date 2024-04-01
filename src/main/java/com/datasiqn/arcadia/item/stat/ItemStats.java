package com.datasiqn.arcadia.item.stat;

import com.datasiqn.arcadia.player.PlayerAttribute;
import org.bukkit.ChatColor;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemStats {
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###.#");

    private final Map<PlayerAttribute, AttributeRange> attributes = new HashMap<>();

    public List<String> asLore(double itemQuality) {
        return asLore(itemQuality, true);
    }
    public List<String> asLore(double itemQuality, boolean space) {
        List<String> lore = new ArrayList<>();
        if (hasAttributes()) {
            for (PlayerAttribute attribute : PlayerAttribute.values()) {
                double value = getAttribute(attribute, itemQuality);
                if (value == 0) continue;
                String sign = value < 0 ? ChatColor.RED + "-" : "+";
                lore.add(ChatColor.GRAY + "" + attribute + ": " + sign + attribute.getColor() + DECIMAL_FORMAT.format(Math.abs(value)) + attribute.getIcon());
            }
        }
        if (space && hasAttributes()) lore.add("");
        return lore;
    }

    public double getAttribute(PlayerAttribute attribute, double itemQuality) {
        AttributeRange range = attributes.get(attribute);
        if (range == null) return 0;
        return range.get(itemQuality);
    }

    public AttributeRange getAttributeRange(PlayerAttribute attribute) {
        return attributes.get(attribute);
    }

    public void setAttribute(PlayerAttribute attribute, AttributeRange range) {
        attributes.put(attribute, range);
    }

    public boolean hasRandomizedAttributes() {
        for (Map.Entry<PlayerAttribute, AttributeRange> entry : attributes.entrySet()) {
            AttributeRange instance = entry.getValue();
            if (instance.hasRange()) return true;
        }
        return false;
    }

    public boolean hasAttributes() {
        return !attributes.isEmpty();
    }
}

