package com.datasiqn.arcadia.entities;

import com.datasiqn.arcadia.Arcadia;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public interface ArcadiaEntitySummoner {
    void summonEntity(@NotNull Location location, Arcadia plugin);
}
