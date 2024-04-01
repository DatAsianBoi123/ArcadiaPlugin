package com.datasiqn.arcadia.item.meta;

import com.datasiqn.arcadia.ArcadiaTag;
import com.datasiqn.arcadia.datatypes.EnchantsDataType;
import com.datasiqn.arcadia.enchants.EnchantType;
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
    private final Object2IntMap<EnchantType> enchants = new Object2IntOpenHashMap<>();

    private double itemQuality;

    public ArcadiaItemMeta(@NotNull UUID uuid) {
        this.uuid = uuid;
        this.itemQuality = new Random(uuid.getMostSignificantBits()).nextDouble();
    }

    public @NotNull UUID getUuid() {
        return uuid;
    }

    public double getItemQuality() {
        return itemQuality;
    }

    public void setItemQuality(double newAmount) {
        this.itemQuality = Math.min(1, newAmount);
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

        PdcUtil.set(pdc, ArcadiaTag.ITEM_QUALITY, itemQuality);
    }

    @Contract("_ -> new")
    public static @NotNull ArcadiaItemMeta fromPdc(@NotNull PersistentDataContainer pdc) {
        UUID uuid = PdcUtil.getOrDefault(pdc, ArcadiaTag.ITEM_UUID, UUID.randomUUID());
        double itemQuality = PdcUtil.getOrDefault(pdc, ArcadiaTag.ITEM_QUALITY, 0d);

        ArcadiaItemMeta itemMeta = new ArcadiaItemMeta(uuid);
        itemMeta.itemQuality = itemQuality;
        EnchantsDataType.EnchantData[] enchants = PdcUtil.getOrDefault(pdc, ArcadiaTag.ITEM_ENCHANTS, new EnchantsDataType.EnchantData[0]);
        for (EnchantsDataType.EnchantData enchantData : enchants) {
            itemMeta.enchants.put(enchantData.enchantType(), enchantData.level());
        }
        return itemMeta;
    }
}
