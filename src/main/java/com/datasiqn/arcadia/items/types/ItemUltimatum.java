package com.datasiqn.arcadia.items.types;

import com.datasiqn.arcadia.items.ItemRarity;
import com.datasiqn.arcadia.items.ItemType;
import com.datasiqn.arcadia.items.abilities.AbilityType;
import com.datasiqn.arcadia.items.abilities.ItemAbility;
import com.datasiqn.arcadia.items.data.ItemData;
import com.datasiqn.arcadia.items.meta.MetaBuilder;
import com.datasiqn.arcadia.items.stats.AttributeRange;
import com.datasiqn.arcadia.items.stats.ItemAttribute;
import com.datasiqn.arcadia.players.ArcadiaSender;
import com.datasiqn.arcadia.players.PlayerStats;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public final class ItemUltimatum implements CustomMaterial {
    private final ItemData itemData = new ItemData(ChatColor.RED + "" + ChatColor.BOLD + "<<" + ChatColor.RED + "Ultimatum" + ChatColor.BOLD + ">>",
            "ULTIMATUM",
            Material.NETHERITE_AXE,
            ItemRarity.MYTHIC,
            true,
            false,
            new ItemAbility("Last Hope", Collections.singletonList("Does cool things"), AbilityType.RIGHT_CLICK, 4000, executor -> {
                PlayerStats playerStats = executor.playerData().playerStats();
                ArcadiaSender<Player> player = executor.playerData().player();

                playerStats.heal();
                playerStats.updateValues();
                playerStats.updateActionbar();
                player.get().getWorld().createExplosion(player.get().getLocation(), 8, false, false, player.get());
            }),
            ItemType.SWORD);

    private final MetaBuilder metaBuilder = new MetaBuilder()
            .setAttribute(ItemAttribute.DAMAGE, new AttributeRange(1500, 3000))
            .setAttribute(ItemAttribute.STRENGTH, 200);

    @Override
    public @NotNull ItemData getItemData() {
        return itemData;
    }

    @Override
    public @NotNull MetaBuilder getMetaBuilder() {
        return metaBuilder;
    }
}
