package com.datasiqn.arcadia.item.abilities;

import com.datasiqn.arcadia.util.lorebuilder.Lore;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class DashAbility extends ItemAbility {
    public DashAbility() {
        super("Dash", Lore.of("Dashes forward, negating all damage while dashing"), 40);
    }

    @Override
    public void execute(@NotNull AbilityExecuteContext context) {
        Player player = context.playerData().getPlayer();
        Vector direction = player.getEyeLocation().getDirection();
        player.teleport(player.getLocation().add(direction.multiply(4)));
        player.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
    }
}
