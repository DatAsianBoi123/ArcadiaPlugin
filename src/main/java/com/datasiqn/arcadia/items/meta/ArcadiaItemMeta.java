package com.datasiqn.arcadia.items.meta;

import com.datasiqn.arcadia.ArcadiaTag;
import com.datasiqn.arcadia.datatype.EnchantsDataType;
import com.datasiqn.arcadia.enchants.EnchantType;
import com.datasiqn.arcadia.items.stats.ItemStats;
import com.datasiqn.arcadia.util.PdcUtil;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public class ArcadiaItemMeta {
    private final UUID uuid;
    private final double itemQuality;
    private final ItemStats itemStats = new ItemStats();
    private final Object2IntMap<EnchantType> enchants = new Object2IntOpenHashMap<>();

    private double qualityBonus;

    public ArcadiaItemMeta(@NotNull UUID uuid) {
        this.uuid = uuid;
        this.itemQuality = new Random(uuid.getMostSignificantBits()).nextDouble();
        this.itemStats.setItemQuality(itemQuality);
    }
    public ArcadiaItemMeta(@NotNull PersistentDataContainer pdc) {
        this.uuid = PdcUtil.getOrDefault(pdc, ArcadiaTag.ITEM_UUID, UUID.randomUUID());
        this.itemQuality = new Random(uuid.getMostSignificantBits()).nextDouble();
        this.qualityBonus = PdcUtil.getOrDefault(pdc, ArcadiaTag.ITEM_QUALITY_BONUS, 0d);
        this.itemStats.setItemQuality(itemQuality);

        EnchantsDataType.EnchantData[] enchantData = PdcUtil.getOrDefault(pdc, ArcadiaTag.ITEM_ENCHANTS, new EnchantsDataType.EnchantData[0]);
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
        return enchants.getInt(type);
    }

    @Contract(" -> new")
    @Unmodifiable
    public @NotNull Set<EnchantType> getEnchants() {
        return Set.copyOf(enchants.keySet());
    }

    public void addEnchant(EnchantType type, int level) {
        enchants.put(type, level);
    }

    public void removeEnchant(EnchantType type) {
        enchants.removeInt(type);
    }

    public void clearEnchants() {
        enchants.clear();
    }

    public void addToPdc(@NotNull PersistentDataContainer pdc) {
        if (hasEnchants()) {
            List<EnchantsDataType.EnchantData> dataList = new ArrayList<>();
            enchants.forEach((type, level) -> dataList.add(new EnchantsDataType.EnchantData(type, level)));
            PdcUtil.set(pdc, ArcadiaTag.ITEM_ENCHANTS, dataList.toArray(EnchantsDataType.EnchantData[]::new));
        }

        if (itemStats.hasAttributes()) {
            PdcUtil.set(pdc, ArcadiaTag.ITEM_QUALITY_BONUS, qualityBonus);
        }
    }
}
