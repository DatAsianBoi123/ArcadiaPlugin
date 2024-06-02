package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.effect.ArcadiaEffectType;
import com.datasiqn.arcadia.entities.ArcadiaEntity;
import com.datasiqn.arcadia.player.PlayerData;
import com.datasiqn.commandcore.argument.annotation.Limit;
import com.datasiqn.commandcore.argument.duration.Duration;
import com.datasiqn.commandcore.argument.selector.EntitySelector;
import com.datasiqn.commandcore.command.annotation.*;
import com.datasiqn.commandcore.command.source.CommandSource;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Consumer;

@CommandDescription(name = "effect", description = "Handles custom Arcadia effects", permission = "arcadia.effect")
public class CommandEffect implements AnnotationCommand {
    private final Arcadia plugin;

    public CommandEffect(Arcadia plugin) {
        this.plugin = plugin;
    }

    @LiteralExecutor("give")
    public void give(CommandSource source,
                     @Argument(name = "entities") @Limit EntitySelector<Entity> entities,
                     @Argument(name = "effect") ArcadiaEffectType effectType,
                     @Argument(name = "duration") Duration duration) {
        PlayerData effector = source.getPlayerChecked().map(player -> plugin.getPlayerManager().getPlayerData(player)).unwrapOr(null);
        forEachArcadiaEntity(entities.get(source), entity -> entity.addArcadiaEffect(effectType, duration.ticks(), effector));
    }

    @LiteralExecutor("clear")
    public void clear(CommandSource source,
                      @Argument(name = "entities") @Limit EntitySelector<Entity> entities,
                      @Argument(name = "effect") @Optional ArcadiaEffectType effectType) {
        forEachArcadiaEntity(entities.get(source), entity -> {
            if (effectType == null) entity.clearArcadiaEffects();
            else entity.removeArcadiaEffect(effectType);
        });
    }

    private void forEachArcadiaEntity(@NotNull Collection<Entity> entities, Consumer<ArcadiaEntity> consumer) {
        for (Entity entity : entities) {
            var nmsEntity = ((CraftEntity) entity).getHandle();
            if (!(nmsEntity instanceof ArcadiaEntity arcadiaEntity)) continue;

            consumer.accept(arcadiaEntity);
        }
    }
}
