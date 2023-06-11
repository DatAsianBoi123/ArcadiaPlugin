package com.datasiqn.arcadia.item.abilities;

import com.datasiqn.arcadia.players.ArcadiaSender;
import com.datasiqn.arcadia.players.PlayerData;
import com.datasiqn.arcadia.util.lorebuilder.Lore;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LastHopeAbility extends ItemAbility {
    public LastHopeAbility() {
        super("Last Hope", Lore.of("Does cool things"), 4000);
    }

    @Override
    public void execute(@NotNull AbilityExecutor executor) {
        PlayerData playerData = executor.playerData();
        ArcadiaSender<Player> player = executor.playerData().getSender();

        playerData.heal();
        playerData.updateValues();
        playerData.updateActionbar();
        player.get().getWorld().createExplosion(player.get().getLocation(), 8, false, false, player.get());
    }
}
