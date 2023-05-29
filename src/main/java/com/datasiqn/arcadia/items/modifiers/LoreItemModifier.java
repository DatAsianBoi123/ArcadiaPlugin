package com.datasiqn.arcadia.items.modifiers;

import com.datasiqn.arcadia.util.lorebuilder.Lore;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LoreItemModifier implements ItemModifier {
    private final Lore lore;

    public LoreItemModifier(@NotNull Lore lore) {
        this.lore = lore;
    }

    @Override
    public void modify(@NotNull UUID uuid, @Nullable ItemMeta metaCopy) {
        if (metaCopy == null) return;
        List<String> itemLore = metaCopy.getLore();
        if (itemLore == null) itemLore = new ArrayList<>();
        lore.addTo(itemLore.size() - 1, itemLore);
        itemLore.add(itemLore.size() - 1, "");
        metaCopy.setLore(itemLore);
    }
}
