package com.datasiqn.arcadia.items.type.data;

import com.datasiqn.arcadia.items.stats.ItemAttribute;
import com.datasiqn.arcadia.players.PlayerData;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public record ConsumableData(List<String> description, double hungerCost, Consumer<PlayerData> eatConsumer) implements ExtraItemData {
    public void eat(PlayerData playerData) {
        eatConsumer.accept(playerData);
    }

    @Override
    public @NotNull List<String> getLore() {
        List<String> lore = new ArrayList<>();
        description.forEach(str -> lore.add(ChatColor.GRAY + str));
        lore.add(ChatColor.DARK_GRAY + "Hunger: " + ItemAttribute.HUNGER.getColor() + hungerCost + ItemAttribute.HUNGER.getIcon());
        return lore;
    }
}
