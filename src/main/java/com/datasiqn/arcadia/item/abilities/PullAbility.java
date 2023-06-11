package com.datasiqn.arcadia.item.abilities;

import com.datasiqn.arcadia.util.lorebuilder.Lore;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class PullAbility extends ItemAbility {
    public PullAbility() {
        super("Pull", Lore.of("Pulls you forward"), 40);
    }

    @Override
    public void execute(@NotNull AbilityExecutor executor) {
        Player player = executor.playerData().getSender().get();
        player.setVelocity(player.getVelocity().add(player.getLocation().getDirection().multiply(2).add(new Vector(0, 0.75, 0))));
        player.playSound(player, Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 1);
        player.spawnParticle(Particle.CLOUD, player.getEyeLocation().add(player.getLocation().getDirection().multiply(0.1)), 15, 0.01, 0.01, 0.01, 0.1);
    }
}
