package com.datasiqn.arcadia.item;

import com.datasiqn.arcadia.item.material.ArcadiaMaterial;
import org.bukkit.Material;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ItemId {
    private final ArcadiaMaterial material;
    private final String id;

    private ItemId(ArcadiaMaterial material, String id) {
        this.material = material;
        this.id = id;
    }

    public String getStringId() {
        return id;
    }

    public ArcadiaMaterial getMaterial() {
        return material;
    }

    @Contract("_ -> new")
    public static @NotNull ItemId fromArcadiaMaterial(@NotNull ArcadiaMaterial material) {
        return new ItemId(material, material.name().toLowerCase());
    }

    public static @NotNull ItemId fromVanillaMaterial(@NotNull Material material) {
        return new ItemId(null, material.name().toLowerCase());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ItemId other)) return false;
        if (obj == this) return true;
        return id.equalsIgnoreCase(other.id);
    }
}
