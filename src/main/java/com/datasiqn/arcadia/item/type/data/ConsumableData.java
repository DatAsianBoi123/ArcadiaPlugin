package com.datasiqn.arcadia.item.type.data;

import com.datasiqn.arcadia.player.AttributeFormats;
import com.datasiqn.arcadia.player.PlayerData;
import com.datasiqn.arcadia.util.lorebuilder.Lore;
import com.datasiqn.arcadia.util.lorebuilder.LoreBuilder;
import com.datasiqn.arcadia.util.lorebuilder.component.ComponentBuilder;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public record ConsumableData(Lore description, double hungerCost, Consumer<PlayerData> eatConsumer) implements ExtraItemData {
    public void eat(PlayerData playerData) {
        eatConsumer.accept(playerData);
    }

    @Override
    public @NotNull Lore getLore() {
        return new LoreBuilder()
                .append(description)
                .append(new ComponentBuilder()
                        .text("Hunger: ", ChatColor.DARK_GRAY)
                        .stat(AttributeFormats.HUNGER, hungerCost)
                        .build())
                .build();
    }
}
