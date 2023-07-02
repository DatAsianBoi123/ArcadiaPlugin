package com.datasiqn.arcadia.item.meta;

import com.datasiqn.arcadia.ArcadiaTag;
import com.datasiqn.arcadia.datatypes.EnchantsDataType;
import com.datasiqn.arcadia.enchants.EnchantType;
import com.datasiqn.arcadia.item.stat.ItemStats;
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
    private final ItemStats itemStats = new ItemStats();
    private final Object2IntMap<EnchantType> enchants = new Object2IntOpenHashMap<>();

    private double itemQuality;

    public ArcadiaItemMeta(@NotNull UUID uuid) {
        this.uuid = uuid;
        this.itemQuality = new Random(uuid.getMostSignificantBits()).nextDouble();
        this.itemStats.setItemQuality(itemQuality);
    }
    public ArcadiaItemMeta(@NotNull PersistentDataContainer pdc) {
        this.uuid = PdcUtil.getOrDefault(pdc, ArcadiaTag.ITEM_UUID, UUID.randomUUID());
        this.itemQuality = PdcUtil.getOrDefault(pdc, ArcadiaTag.ITEM_QUALITY, 0d);
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

    public void setItemQuality(double newAmount) {
        this.itemQuality = Math.min(1, newAmount);
        this.itemStats.setItemQuality(itemQuality);
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
            PdcUtil.set(pdc, ArcadiaTag.ITEM_QUALITY, itemQuality);
        }
    }
}
