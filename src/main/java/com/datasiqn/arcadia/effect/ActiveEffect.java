package com.datasiqn.arcadia.effect;

import com.datasiqn.commandcore.argument.duration.Duration;
import org.jetbrains.annotations.NotNull;

public class ActiveEffect {
    private final ArcadiaEffect effect;
    private final Duration duration;
    private long tickNumber = 0;

    public ActiveEffect(@NotNull ArcadiaEffect effect, Duration duration) {
        this.effect = effect;
        this.duration = duration;

        effect.begin();
    }

    public boolean tick() {
        if (tickNumber >= duration.ticks()) {
            if (tickNumber == duration.ticks()) {
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
        tickNumber = duration.ticks() + 1;
    }

    public ArcadiaEffect getEffect() {
        return effect;
    }
}