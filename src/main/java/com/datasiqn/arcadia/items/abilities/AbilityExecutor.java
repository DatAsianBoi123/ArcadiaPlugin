package com.datasiqn.arcadia.items.abilities;

import com.datasiqn.arcadia.managers.PlayerManager;
import org.jetbrains.annotations.NotNull;

public interface AbilityExecutor {
    @NotNull PlayerManager.PlayerData playerData();
    @NotNull ItemAbility ability();
}
