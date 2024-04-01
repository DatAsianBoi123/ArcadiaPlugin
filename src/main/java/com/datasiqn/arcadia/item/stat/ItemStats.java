package com.datasiqn.arcadia.item.stat;

import com.datasiqn.arcadia.player.PlayerAttribute;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: change how item quality is handled in here
public class ItemStats {
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###.#");

    private final Map<PlayerAttribute, AttributeInstance> itemAttributes = new HashMap<>();
    private double itemQuality;

    public List<String> asLore() {
        return asLore(true);
    }
    public List<String> asLore(boolean space) {
        List<String> lore = new ArrayList<>();
        if (hasAttributes()) {
            for (PlayerAttribute attribute : PlayerAttribute.values()) {
                AttributeInstance attributeInstance = getAttribute(attribute);
                if (attributeInstance == null) continue;
                double value = attributeInstance.getValue();
                if (value == 0) continue;
                String sign = value < 0 ? ChatColor.RED + "-" : "+";
                lore.add(ChatColor.GRAY + "" + attribute.getItemAttribute() + ": " + sign + attribute.getItemAttribute().getColor() + DECIMAL_FORMAT.format(Math.abs(value)) + attribute.getItemAttribute().getIcon());
            }
        }
        if (space && hasAttributes()) lore.add("");
        return lore;
    }

    @Nullable
    public AttributeInstance getAttribute(PlayerAttribute attribute) {
        AttributeInstance instance = itemAttributes.get(attribute);
        if (instance == null) return null;
        instance.setItemQuality(itemQuality);
        return instance;
    }

    public void setAttribute(PlayerAttribute attribute, double value) {
        setAttribute(attribute, new AttributeInstance(value));
    }
    public void setAttribute(PlayerAttribute attribute, AttributeRange range) {
        setAttribute(attribute, new AttributeInstance(range));
    }
    public void setAttribute(PlayerAttribute attribute, AttributeInstance instance) {
        itemAttributes.put(attribute, instance);
    }

    public boolean hasRandomizedAttributes() {
        for (Map.Entry<PlayerAttribute, AttributeInstance> entry : itemAttributes.entrySet()) {
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

