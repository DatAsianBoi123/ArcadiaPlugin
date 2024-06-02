package com.datasiqn.arcadia.item.abilities;

import com.datasiqn.arcadia.util.lorebuilder.Lore;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class RunAwayAbility extends ItemAbility {
    public RunAwayAbility() {
        super("Run Away", Lore.of("Grants you speed for a few seconds"), 60);
    }

    @Override
    public void execute(@NotNull AbilityExecuteContext context) {
        Player player = context.playerData().getPlayer();
        player.playSound(player, Sound.BLOCK_AMETHYST_BLOCK_PLACE, 1, 1);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 1));
    }
}
