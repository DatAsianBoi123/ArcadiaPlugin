package com.datasiqn.arcadia.upgrades;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.items.ItemRarity;
import com.datasiqn.arcadia.upgrades.listeners.BloodChaliceListener;
import com.datasiqn.arcadia.upgrades.listeners.UpgradeListener;
import com.datasiqn.arcadia.util.lorebuilder.Lore;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public enum UpgradeType {
    BLOOD_CHALICE("Blood Chalice", Lore.of("Killing enemies heal you"), Material.POTION, ItemRarity.RARE, new BloodChaliceListener()),
    ;

    private final String displayName;
    private final Lore description;
    private final Material material;
    private final ItemRarity rarity;

    UpgradeType(String displayName, @NotNull Lore description, Material material, ItemRarity rarity, UpgradeListener listener) {
        this.displayName = displayName;
        this.description = description;
        this.material = material;
        this.rarity = rarity;

        Arcadia.getPlugin(Arcadia.class).getUpgradeEventManager().register(listener, this);
    }

    public String getDisplayName() {
        return displayName;
    }

    public Lore getDescription() {
        return description;
    }

    public Material getMaterial() {
        return material;
    }

    public ItemRarity getRarity() {
        return rarity;
    }
}
