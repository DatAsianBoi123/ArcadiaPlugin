package com.datasiqn.arcadia.items.modifiers;

import com.datasiqn.arcadia.items.data.ItemData;
import org.bukkit.ChatColor;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class LoreItemModifier implements ItemModifier {
    private final List<String> lore;

    public LoreItemModifier(String... lore) {
        this.lore = Arrays.stream(lore).map(s -> ChatColor.GRAY + s).toList();
    }

    @Override
    public @Nullable ItemMeta modify(@NotNull ItemData itemData, @NotNull UUID uuid, @Nullable ItemMeta metaCopy) {
        if (metaCopy == null) return null;
        List<String> itemLore = metaCopy.getLore();
        if (itemLore == null) itemLore = new ArrayList<>();
        itemLore.addAll(itemLore.size() - 1, lore);
        itemLore.add(itemLore.size() - 1, "");
        metaCopy.setLore(itemLore);
        return metaCopy;
    }
}
