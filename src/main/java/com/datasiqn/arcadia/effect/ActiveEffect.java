package com.datasiqn.arcadia.effect;

import org.jetbrains.annotations.NotNull;

public class ActiveEffect {
    private final ArcadiaEffect effect;
    private final long duration;
    private long tickNumber = 0;

    public ActiveEffect(@NotNull ArcadiaEffect effect, long duration) {
        this.effect = effect;
        this.duration = duration;

        effect.begin();
    }

    public boolean tick() {
        if (tickNumber >= duration) {
            if (tickNumber == duration) {
                effect.end();
                tickNumber++;
            }
            return true;
        }
        effect.tick();
        tickNumber++;
        return false;
    }

    public void end() {
        effect.end();
        tickNumber = duration;
    }

    public ArcadiaEffect getEffect() {
        return effect;
    }
}
