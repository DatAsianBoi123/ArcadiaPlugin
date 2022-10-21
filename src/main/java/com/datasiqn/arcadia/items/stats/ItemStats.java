package com.datasiqn.arcadia.items.stats;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemStats {
    private final Map<ItemAttribute, AttributeInstance> itemAttributes = new HashMap<>();
    private double itemQuality;

    public List<String> asLore() {
        return asLore(true);
    }
    public List<String> asLore(boolean space) {
        List<String> lore = new ArrayList<>();
        DecimalFormat decimalFormat = new DecimalFormat("#,###.#");
        if (hasAttributes()) {
            for (ItemAttribute attribute : ItemAttribute.values()) {
                AttributeInstance attributeInstance = getAttribute(attribute);
                if (attributeInstance == null) continue;
                lore.add(ChatColor.GRAY + "" + attribute + ": +" + attribute.getColor() + decimalFormat.format(attributeInstance.getValue()) + attribute.getIcon());
            }
        }
        if (space && hasAttributes()) lore.add("");
        return lore;
    }

    @Nullable
    public AttributeInstance getAttribute(ItemAttribute attribute) {
        AttributeInstance instance = itemAttributes.get(attribute);
        if (instance == null) return null;
        instance.setItemQuality(itemQuality);
        return instance;
    }

    public void setAttribute(ItemAttribute attribute, double value) {
        setAttribute(attribute, new AttributeInstance(value));
    }
    public void setAttribute(ItemAttribute attribute, AttributeRange range) {
        setAttribute(attribute, new AttributeInstance(range));
    }
    public void setAttribute(ItemAttribute attribute, AttributeInstance instance) {
        itemAttributes.put(attribute, instance);
    }

    public boolean hasRandomizedAttributes() {
        for (Map.Entry<ItemAttribute, AttributeInstance> entry : itemAttributes.entrySet()) {
            AttributeInstance instance = entry.getValue();
            if (instance.isRandom()) return true;
        }
        return false;
    }

    public void setItemQuality(double itemQuality) {
        this.itemQuality = itemQuality;
    }

    public boolean hasAttributes() {
        return !itemAttributes.isEmpty();
    }
}

