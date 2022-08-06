package com.datasiqn.arcadia.items.modifiers;

import com.datasiqn.arcadia.items.data.ItemData;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface ItemModifier {
    @Contract("_, _, !null -> param3; _, _, null -> null")
    @Nullable ItemMeta modify(@NotNull ItemData itemData, @NotNull UUID uuid, @Nullable ItemMeta metaCopy);
}
