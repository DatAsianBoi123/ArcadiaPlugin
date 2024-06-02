package com.datasiqn.arcadia.effect;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.entities.ArcadiaEntity;
import com.datasiqn.arcadia.player.PlayerData;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ArcadiaEffectType {
    BURNING(BurnEffect::new),
    ;

    private final EffectProducer producer;

    ArcadiaEffectType(EffectProducer producer) {
        this.producer = producer;
    }

    @Contract("_, _, _, _ -> new")
    public @NotNull ActiveEffect bind(@NotNull ArcadiaEntity entity, @Nullable PlayerData effector, long duration, @NotNull Arcadia plugin) {
        return new ActiveEffect(producer.bind(entity, effector, plugin), duration);
    }

    @FunctionalInterface
    public interface EffectProducer {
        ArcadiaEffect bind(@NotNull ArcadiaEntity entity, @Nullable PlayerData effector, @NotNull Arcadia plugin);
    }
}
