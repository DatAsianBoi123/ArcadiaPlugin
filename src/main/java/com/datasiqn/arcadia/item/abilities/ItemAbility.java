package com.datasiqn.arcadia.item.abilities;

import com.datasiqn.arcadia.util.lorebuilder.Lore;
import org.jetbrains.annotations.NotNull;

public abstract class ItemAbility {
    private final String name;
    private final Lore description;
    private final long cooldown;

    protected ItemAbility(String name, Lore description, long cooldown) {
        this.name = name;
        this.description = description;
        this.cooldown = cooldown;
    }

    public String getName() {
        return name;
    }

    public Lore getDescription() {
        return description;
    }

    public long getCooldown() {
        return cooldown;
    }

    public abstract void execute(@NotNull AbilityExecutor executor);
}
