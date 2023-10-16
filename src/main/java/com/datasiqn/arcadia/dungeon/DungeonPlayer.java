package com.datasiqn.arcadia.dungeon;

import com.datasiqn.arcadia.player.ArcadiaSender;
import com.datasiqn.arcadia.player.PlayerData;
import com.datasiqn.arcadia.upgrade.Upgrade;
import com.datasiqn.arcadia.upgrade.UpgradeType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class DungeonPlayer {
    private final List<Upgrade> upgrades = new ArrayList<>();
    private final PlayerData playerData;
    private int totalUpgrades = 0;

    public DungeonPlayer(PlayerData playerData) {
        this.playerData = playerData;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public Player getPlayer() {
        return playerData.getPlayer();
    }

    public ArcadiaSender<Player> getSender() {
        return playerData.getSender();
    }

    public UUID getUniqueId() {
        return playerData.getUniqueId();
    }

    public void pickupUpgrade(UpgradeType upgradeType) {
        pickupUpgrade(upgradeType, 1);
    }
    public void pickupUpgrade(UpgradeType upgradeType, int amount) {
        for (Upgrade upgrade : upgrades) {
            if (upgrade.getType() == upgradeType) {
                totalUpgrades += amount;
                upgrade.setAmount(upgrade.getAmount() + amount);
                return;
            }
        }
        totalUpgrades++;
        upgrades.add(new Upgrade(upgradeType, amount));
    }

    @UnmodifiableView
    @NotNull
    public List<Upgrade> getUpgrades() {
        return Collections.unmodifiableList(upgrades);
    }

    public int getTotalUpgrades() {
        return totalUpgrades;
    }

    public int getUpgradeAmount(UpgradeType upgradeType) {
        return upgrades.stream().filter(upgrade -> upgrade.getType() == upgradeType).findFirst().map(Upgrade::getAmount).orElse(0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DungeonPlayer that = (DungeonPlayer) o;

        return playerData.equals(that.playerData);
    }
}
