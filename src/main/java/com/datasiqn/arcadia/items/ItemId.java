package com.datasiqn.arcadia.items;

import com.datasiqn.arcadia.items.materials.ArcadiaMaterial;
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
}
