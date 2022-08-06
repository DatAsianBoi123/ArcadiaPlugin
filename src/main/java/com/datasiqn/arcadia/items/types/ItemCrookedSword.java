package com.datasiqn.arcadia.items.types;

import com.datasiqn.arcadia.items.ItemRarity;
import com.datasiqn.arcadia.items.ItemType;
import com.datasiqn.arcadia.items.abilities.AbilityType;
import com.datasiqn.arcadia.items.abilities.ItemAbility;
import com.datasiqn.arcadia.items.data.ItemData;
import com.datasiqn.arcadia.items.meta.MetaBuilder;
import com.datasiqn.arcadia.items.stats.AttributeInstance;
import com.datasiqn.arcadia.items.stats.AttributeRange;
import com.datasiqn.arcadia.items.stats.ItemAttribute;
import com.datasiqn.arcadia.items.stats.ItemStats;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public final class ItemCrookedSword implements CustomMaterial {
    private final ItemData itemData = new ItemData("Crooked Sword",
            "CROOKED_SWORD",
            Material.WOODEN_SWORD,
            ItemRarity.COMMON,
            false,
            false,
            new ItemAbility("Run Away", Collections.singletonList("Gives you speed"), AbilityType.RIGHT_CLICK, 60, executor -> {
                Player player = executor.playerData().player().get();
                player.playSound(player, Sound.BLOCK_AMETHYST_BLOCK_PLACE, 1, 1);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 1));
            }),
            ItemType.SWORD);

    private final MetaBuilder metaBuilder = new MetaBuilder()
            .setAttribute(ItemAttribute.DAMAGE, new AttributeRange(3, 5))
            .setAttribute(ItemAttribute.DEFENSE, 5)
            .setAttribute(ItemAttribute.STRENGTH, (new AttributeRange(5, 10)));

    @Override
    public @NotNull ItemData getItemData() {
        return itemData;
    }

    @Override
    public @NotNull MetaBuilder getMetaBuilder() {
        return metaBuilder;
    }
}
