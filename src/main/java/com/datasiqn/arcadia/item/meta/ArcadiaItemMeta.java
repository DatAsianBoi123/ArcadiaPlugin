package com.datasiqn.arcadia.item.meta;

import com.datasiqn.arcadia.ArcadiaTag;
import com.datasiqn.arcadia.util.PdcUtil;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.UUID;

public class ArcadiaItemMeta {
    private final UUID uuid;

    private double itemQuality;

    public ArcadiaItemMeta(@NotNull UUID uuid) {
        this(uuid, new Random(uuid.getMostSignificantBits()).nextDouble());
    }
    public ArcadiaItemMeta(@NotNull UUID uuid, double itemQuality) {
        this.uuid = uuid;
        this.itemQuality = itemQuality;
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

    @Contract("_ -> new")
    public static @NotNull ArcadiaItemMeta fromPdc(@NotNull PersistentDataContainer pdc) {
        UUID uuid = PdcUtil.getOrDefault(pdc, ArcadiaTag.ITEM_UUID, UUID.randomUUID());
        Double itemQuality = PdcUtil.get(pdc, ArcadiaTag.ITEM_QUALITY);
        if (itemQuality == null) return new ArcadiaItemMeta(uuid);
        return new ArcadiaItemMeta(uuid, itemQuality);
    }
}
