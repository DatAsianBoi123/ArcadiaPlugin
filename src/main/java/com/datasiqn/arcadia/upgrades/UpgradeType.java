package com.datasiqn.arcadia.upgrades;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.items.ItemRarity;
import com.datasiqn.arcadia.upgrades.listeners.BloodChaliceListener;
import com.datasiqn.arcadia.upgrades.listeners.UpgradeListener;
import com.datasiqn.arcadia.util.lorebuilder.LoreBuilder;
import com.datasiqn.arcadia.util.lorebuilder.component.ComponentBuilder;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public enum UpgradeType {
    DRUGS("Blood Chalice", new LoreBuilder()
            .append(new ComponentBuilder()
                    .text("Killing enemies heal you")
                    .build()),
            Material.POTION, ItemRarity.RARE, new BloodChaliceListener()),
    ;

    private final String displayName;
    private final List<String> description;
    private final Material material;
    private final ItemRarity rarity;

    UpgradeType(String displayName, @NotNull LoreBuilder description, Material material, ItemRarity rarity, UpgradeListener listener) {
        this.displayName = displayName;
        this.description = description.build();
        this.material = material;
        this.rarity = rarity;

        Arcadia.getPlugin(Arcadia.class).getUpgradeEventManager().register(listener, this);
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getDescription() {
        return description;
    }

    public Material getMaterial() {
        return material;
    }

    public ItemRarity getRarity() {
        return rarity;
    }
}
