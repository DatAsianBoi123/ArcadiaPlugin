package com.datasiqn.arcadia.managers;

import com.datasiqn.arcadia.item.ArcadiaItem;
import com.datasiqn.arcadia.item.abilities.AbilityType;
import com.datasiqn.arcadia.item.abilities.ItemAbility;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AbilityCooldownManager {
    private final Map<UUID, Object2LongMap<String>> cooldowns = new HashMap<>();

    public long activateAbility(@NotNull Player player, @NotNull ArcadiaItem item, @NotNull ItemAbility ability, AbilityType abilityType) {
        Object2LongMap<String> abilityCooldowns = cooldowns.computeIfAbsent(player.getUniqueId(), key -> new Object2LongOpenHashMap<>());
        String key = item.getId().getStringId() + "-" + abilityType;
        long lastUsed = abilityCooldowns.getOrDefault(key, -1);
        long currentTime = System.currentTimeMillis();
        long cooldown = ability.getCooldown();
        if (lastUsed == -1 || currentTime - lastUsed >= cooldown * 50) {
            abilityCooldowns.put(key, currentTime);
            return -1;
        }
        return cooldown * 50 - (currentTime - lastUsed);
    }
}
