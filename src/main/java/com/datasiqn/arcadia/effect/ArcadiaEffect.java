package com.datasiqn.arcadia.effect;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.entities.ArcadiaEntity;
import com.datasiqn.arcadia.player.PlayerData;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ArcadiaEffect {
    protected final @NotNull ArcadiaEntity entity;
    protected final @Nullable PlayerData effector;
    protected final @NotNull Arcadia plugin;

    public ArcadiaEffect(@NotNull ArcadiaEntity entity, @Nullable PlayerData effector, @NotNull Arcadia plugin) {
        this.entity = entity;
        this.effector = effector;
        this.plugin = plugin;
    }

    public void begin() {}

    public abstract void tick(int stacks);

    public void end() {}

    public abstract String getIcon();

    public abstract ChatColor getColor();
}
