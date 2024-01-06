package com.datasiqn.arcadia.amulet;

import com.datasiqn.arcadia.item.ArcadiaItem;
import com.datasiqn.arcadia.item.ItemRarity;
import com.datasiqn.arcadia.item.meta.ArcadiaItemMeta;
import com.datasiqn.arcadia.item.stat.ItemStats;
import com.datasiqn.arcadia.managers.LevelRewardManager;
import com.datasiqn.arcadia.player.PlayerAttribute;
import com.datasiqn.arcadia.util.lorebuilder.component.TextLoreComponent;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public enum PowerStone {
    STRENGTH_STONE(PowerStoneData.builder("Strength Stone")
            .material(Material.REDSTONE)
            .addAttribute(PlayerAttribute.STRENGTH, 1)
            .levelRequirement(5)
            .build()),
    HEALTH_STONE(PowerStoneData.builder("Health Stone")
            .material(Material.GOLDEN_APPLE)
            .addAttribute(PlayerAttribute.MAX_HEALTH, 10)
            .levelRequirement(5)
            .build()),

    SPEED_STONE(PowerStoneData.builder("Speed Stone")
            .material(Material.RABBIT_FOOT)
            .rarity(ItemRarity.RARE)
            .addAttribute(PlayerAttribute.SPEED, 0.2)
            .levelRequirement(15)
            .build()),

    ATTACK_SPEED_STONE(PowerStoneData.builder("Attack Speed Stone")
            .material(Material.IRON_SWORD)
            .rarity(ItemRarity.LEGENDARY)
            .addAttribute(PlayerAttribute.ATTACK_SPEED, 25)
            .levelRequirement(30)
            .build()),
    ;

    private final PowerStoneData data;
    private final ArcadiaItem item;

    PowerStone(@NotNull PowerStoneData data) {
        this.data = data;
        ArcadiaItemMeta meta = new ArcadiaItemMeta(UUID.nameUUIDFromBytes(data.getName().getBytes()));
        ItemStats itemStats = meta.getItemStats();
        data.getAttributes().forEach((attribute, value) -> itemStats.setAttribute(attribute.getItemAttribute(), value));
        this.item = new ArcadiaItem(data, meta);
    }

    public PowerStoneData getData() {
        return data;
    }

    public @NotNull ArcadiaItem getItem() {
        return item;
    }

    public static void addRewards(LevelRewardManager levelRewardManager) {
        for (PowerStone powerStone : values()) {
            PowerStoneData stoneData = powerStone.data;
            ItemMeta meta = stoneData.toItemStack().getItemMeta();
            String name = stoneData.getName();
            if (meta != null && meta.hasDisplayName()) name = meta.getDisplayName();
            levelRewardManager.addReward(stoneData.getLevelRequirement(), TextLoreComponent.text(name));
        }
    }
}
