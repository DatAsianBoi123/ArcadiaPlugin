package com.datasiqn.arcadia.effect;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.dungeon.DungeonPlayer;
import com.datasiqn.arcadia.entities.ArcadiaEntity;
import com.datasiqn.arcadia.player.PlayerData;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;

public class BurnEffect extends ArcadiaEffect {
    public static final long TICKS_BETWEEN_HIT = 20;

    private long tickCounter = 0;

    public BurnEffect(ArcadiaEntity entity, PlayerData effector, Arcadia plugin) {
        super(entity, effector, plugin);
    }

    @Override
    public void begin() {
        entity.getBukkitEntity().setVisualFire(true);
    }

    @Override
    public void tick() {
        if (tickCounter > 0) {
            tickCounter--;
            return;
        }
        DamageSource damageSource = entity.damageSources().generic();
        DungeonPlayer dungeonPlayer = null;
        if (effector != null) {
            ServerPlayer nmsPlayer = ((CraftPlayer) effector.getPlayer()).getHandle();
            damageSource = entity.damageSources().playerAttack(nmsPlayer);
            dungeonPlayer = plugin.getDungeonManager().getDungeonPlayer(effector);
        }
        entity.damage(5, damageSource, dungeonPlayer, false);
        tickCounter = TICKS_BETWEEN_HIT;
    }

    @Override
    public void end() {
        entity.getBukkitEntity().setVisualFire(false);
    }

    @Override
    public String getIcon() {
        return "🔥";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.GOLD;
    }
}
