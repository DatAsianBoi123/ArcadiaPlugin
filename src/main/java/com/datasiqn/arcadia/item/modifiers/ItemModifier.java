package com.datasiqn.arcadia.item.modifiers;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface ItemModifier {
    void modify(@NotNull UUID uuid, @Nullable ItemMeta metaCopy);
}
