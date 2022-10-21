package com.datasiqn.arcadia.items.meta;

import com.datasiqn.arcadia.ArcadiaKeys;
import com.datasiqn.arcadia.datatypes.EnchantsDataType;
import com.datasiqn.arcadia.enchants.EnchantType;
import com.datasiqn.arcadia.items.stats.ItemStats;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public class ArcadiaItemMeta {
    private final UUID uuid;
    private final ItemStats itemStats = new ItemStats();
    private final double itemQuality;
    private final Map<EnchantType, Integer> enchants = new HashMap<>();

    private double qualityBonus;

    public ArcadiaItemMeta(@NotNull UUID uuid) {
        this.uuid = uuid;
        this.itemQuality = new Random(uuid.getMostSignificantBits()).nextDouble();
        this.itemStats.setItemQuality(itemQuality);
    }
    public ArcadiaItemMeta(@NotNull ItemMeta meta) {
        this(meta.getPersistentDataContainer());
    }
    public ArcadiaItemMeta(@NotNull PersistentDataContainer pdc) {
        String strUuid = pdc.get(ArcadiaKeys.ITEM_UUID, PersistentDataType.STRING);
        UUID metaUuid;
        if (strUuid == null) {
            metaUuid = UUID.randomUUID();
        } else {
            try {
                metaUuid = UUID.fromString(strUuid);
            } catch (IllegalArgumentException e) {
                metaUuid = UUID.randomUUID();
            }
        }
        this.uuid = metaUuid;
        this.itemQuality = new Random(uuid.getMostSignificantBits()).nextDouble();
        this.qualityBonus = pdc.getOrDefault(ArcadiaKeys.ITEM_QUALITY_BONUS, PersistentDataType.DOUBLE, 0d);
        this.itemStats.setItemQuality(itemQuality);

        EnchantsDataType.EnchantData[] enchantData = pdc.getOrDefault(ArcadiaKeys.ITEM_ENCHANTS, new EnchantsDataType(), new EnchantsDataType.EnchantData[0]);
        for (EnchantsDataType.EnchantData data : enchantData) {
            enchants.put(data.enchantType(), data.level());
        }
    }

    public @NotNull UUID getUuid() {
        return uuid;
    }

    public @NotNull ItemStats getItemStats() {
        return itemStats;
    }

    public double getItemQuality() {
        return itemQuality;
    }

    public double getItemQualityBonus() {
        return qualityBonus;
    }

    public void setItemQualityBonus(double newAmount) {
        this.qualityBonus = Math.min(1 - itemQuality, newAmount);
        this.itemStats.setItemQuality(itemQuality + qualityBonus);
    }

    public boolean hasEnchants() {
        return !enchants.isEmpty();
    }

    public boolean hasEnchant(EnchantType type) {
        return enchants.containsKey(type);
    }

    public int getEnchantLevel(EnchantType type) {
        Integer integer = enchants.get(type);
        return integer == null ? 0 : integer;
    }

    @Contract(" -> new")
    @Unmodifiable
    public @NotNull Map<EnchantType, Integer> getEnchants() {
        return Map.copyOf(enchants);
    }

    public void addEnchant(EnchantType type, int level) {
        enchants.put(type, level);
    }

    public void removeEnchant(EnchantType type) {
        enchants.remove(type);
    }

    public void clearEnchants() {
        enchants.clear();
    }

    public void addToPdc(@NotNull PersistentDataContainer pdc) {
        if (hasEnchants()) {
            List<EnchantsDataType.EnchantData> dataList = new ArrayList<>();
            enchants.forEach((type, level) -> dataList.add(new EnchantsDataType.EnchantData(type, level)));
            pdc.set(ArcadiaKeys.ITEM_ENCHANTS, new EnchantsDataType(), dataList.toArray(new EnchantsDataType.EnchantData[0]));
        }

        if (itemStats.hasAttributes()) {
            pdc.set(ArcadiaKeys.ITEM_QUALITY_BONUS, PersistentDataType.DOUBLE, qualityBonus);
        }
    }
}
