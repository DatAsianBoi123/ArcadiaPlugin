package com.datasiqn.arcadia.item.stat;

import com.datasiqn.arcadia.player.PlayerAttribute;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemStats {
    private final Map<PlayerAttribute, AttributeRange> attributes = new HashMap<>();

    public List<String> asLore(double itemQuality) {
        List<String> lore = new ArrayList<>();
        if (hasAttributes()) {
            for (PlayerAttribute attribute : PlayerAttribute.values()) {
                AttributeRange attributeRange = attributes.get(attribute);
                if (attributeRange == null) continue;
                lore.add(ChatColor.GRAY + "" + attribute + ": " + attributeRange.getFormatted(itemQuality));
            }
        }
        if (hasAttributes()) lore.add("");
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

