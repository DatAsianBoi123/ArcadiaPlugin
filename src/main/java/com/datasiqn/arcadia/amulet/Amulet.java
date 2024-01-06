package com.datasiqn.arcadia.amulet;

import com.datasiqn.arcadia.managers.LevelRewardManager;
import com.datasiqn.arcadia.player.PlayerData;
import com.datasiqn.arcadia.util.lorebuilder.component.ComponentBuilder;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Iterator;

public class Amulet implements Iterable<PowerStone> {
    private static final int[] SLOT_UPGRADES = { 5, 10, 20, 30, 40, 60, 80, 110, 150 };

    private final @Nullable PowerStone @NotNull [] powerStones = new PowerStone[9];
    private final PlayerData playerData;

    public Amulet(PlayerData playerData) {
        this.playerData = playerData;
    }

    public @Nullable PowerStone get(int index) {
        return powerStones[index];
    }

    public int add(PowerStone powerStone) {
        if (contains(powerStone)) return -1;
        for (int i = 0; i < getTotalSlots(); i++) {
            if (powerStones[i] != null) continue;
            powerStones[i] = powerStone;
            return i;
        }
        return -1;
    }

    public boolean set(int index, PowerStone powerStone) {
        if (index >= getTotalSlots()) return false;
        if (contains(powerStone)) return false;
        powerStones[index] = powerStone;
        return true;
    }

    public void delete(int index) {
        powerStones[index] = null;
    }

    public void clear() {
        for (int i = 0; i < powerStones.length; i++) {
            delete(i);
        }
    }

    public boolean contains(PowerStone powerStone) {
        for (PowerStone stone : powerStones) {
            if (stone == powerStone) return true;
        }
        return false;
    }

    public int getTotalSlots() {
        for (int i = SLOT_UPGRADES.length - 1; i >= 0; i--) {
            if (playerData.getXp().getLevel() >= SLOT_UPGRADES[i]) return i + 1;
        }
        return 0;
    }

    public int getLevelForSlots(int slots) {
        if (slots == 0) return 0;
        return SLOT_UPGRADES[slots - 1];
    }

    @NotNull
    @Override
    public Iterator<PowerStone> iterator() {
        return Arrays.stream(powerStones).iterator();
    }

    public static void addRewards(LevelRewardManager levelRewardManager) {
        for (int level : SLOT_UPGRADES) {
            levelRewardManager.addReward(level, new ComponentBuilder()
                    .text("+").number(1).text(" Amulet ", ChatColor.BLUE).text("Slot")
                    .build());
        }
    }
}
