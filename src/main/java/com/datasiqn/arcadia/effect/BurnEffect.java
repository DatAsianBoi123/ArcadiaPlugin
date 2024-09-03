package com.datasiqn.arcadia.effect;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.damage.DamageCause;
import com.datasiqn.arcadia.entities.ArcadiaEntity;
import com.datasiqn.arcadia.player.PlayerData;
import net.md_5.bungee.api.ChatColor;

public class BurnEffect extends ArcadiaEffect {
    public static final long TICKS_BETWEEN_HIT = 20;

    private long tickCounter = TICKS_BETWEEN_HIT;

    public BurnEffect(ArcadiaEntity entity, PlayerData effector, Arcadia plugin) {
        super(entity, effector, plugin);
    }

    @Override
    public void begin() {
        entity.getBukkitEntity().setVisualFire(true);
    }

    @Override
    public void tick(int stacks) {
        if (tickCounter > 0) {
            tickCounter--;
            return;
        }
        DamageCause damageCause;
        if (effector != null) {
            damageCause = DamageCause.indirect(plugin.getDungeonManager().getDungeonPlayer(effector), entity.damageSources().onFire());
        } else {
            damageCause = DamageCause.natural(entity.damageSources().onFire());
        }
        entity.damage(5 * stacks, damageCause, false);
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
