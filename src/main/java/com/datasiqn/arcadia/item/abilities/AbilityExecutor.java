package com.datasiqn.arcadia.item.abilities;

import com.datasiqn.arcadia.players.PlayerData;
import org.jetbrains.annotations.NotNull;

public interface AbilityExecutor {
    @NotNull PlayerData playerData();
    @NotNull ItemAbility ability();
}
