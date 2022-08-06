package com.datasiqn.arcadia.items.stats;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemStats {
    private final Map<ItemAttribute, AttributeInstance> itemAttributes = new HashMap<>();

    public List<String> asLore() {
        return asLore(true);
    }
    public List<String> asLore(boolean space) {
        List<String> lore = new ArrayList<>();
        DecimalFormat decimalFormat = new DecimalFormat("#,###.#");
        if (hasAttributes()) {
            for (ItemAttribute attribute : ItemAttribute.values()) {
                AttributeInstance attributeInstance = itemAttributes.get(attribute);
                if (attributeInstance == null) continue;
                lore.add(ChatColor.GRAY + "" + attribute + ": +" + attribute.getColor() + decimalFormat.format(attributeInstance.getValue()) + attribute.getIcon());
            }
        }
        if (space && hasAttributes()) lore.add("");
        return lore;
    }

    @Nullable
    public AttributeInstance getAttribute(ItemAttribute attribute) {
        return itemAttributes.get(attribute);
    }

    @Contract("_, _ -> this")
    public ItemStats setAttribute(ItemAttribute attribute, AttributeInstance instance) {
        itemAttributes.put(attribute, instance);
        return this;
    }

    public boolean hasRandomizedAttributes() {
        for (Map.Entry<ItemAttribute, AttributeInstance> entry : itemAttributes.entrySet()) {
            AttributeInstance instance = entry.getValue();
            if (instance.isRandom()) return true;
        }
        return false;
    }

    public void setItemQuality(double itemQuality) {
        itemAttributes.forEach((attribute, instance) -> instance.setItemQuality(itemQuality));
    }

    public boolean hasAttributes() {
        return !itemAttributes.isEmpty();
    }
}

