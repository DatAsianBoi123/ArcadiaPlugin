package com.datasiqn.arcadia.upgrade;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.item.ItemRarity;
import com.datasiqn.arcadia.item.modifiers.LoreItemModifier;
import com.datasiqn.arcadia.item.modifiers.PotionModifier;
import com.datasiqn.arcadia.rand.WeightedRandom;
import com.datasiqn.arcadia.upgrade.listeners.*;
import com.datasiqn.arcadia.util.lorebuilder.Lore;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.objects.Object2DoubleLinkedOpenHashMap;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

public enum UpgradeType {
    //<editor-fold desc="Common Upgrades">
    BLOOD_CHALICE(UpgradeData.builder()
            .name("Blood Chalice")
            .material(Material.POTION)
            .rarity(ItemRarity.COMMON)
            .addModifier(new LoreItemModifier(Lore.of("Killing enemies heal you")))
            .addModifier(new PotionModifier(Color.RED))
            .build(), new BloodChaliceListener()),
    //</editor-fold>

    //<editor-fold desc="Rare Upgrades">,
    MAGIC_QUIVER(UpgradeData.builder()
            .name("Magic Quiver")
            .material(Material.LEATHER_HORSE_ARMOR)
            .rarity(ItemRarity.RARE)
            .addModifier(new LoreItemModifier(Lore.of("Bows shoot extra arrows")))
            .build(), new MagicQuiverListener()),
    LIGHTNING_BOTTLE(UpgradeData.builder()
            .name("Lightning in a Bottle")
            .material(Material.POTION)
            .rarity(ItemRarity.RARE)
            .addModifier(new LoreItemModifier(Lore.of("Chance on hit to strike lightning")))
            .addModifier(new PotionModifier(Color.BLUE))
            .build(), new LightningBottleListener()),
    //</editor-fold>

    //<editor-fold desc="Legendary Upgrades">
    UPGRADE_COMPRESSOR(UpgradeData.builder()
            .name("Upgrade Compressor")
            .material(Material.PISTON)
            .rarity(ItemRarity.LEGENDARY)
            .addModifier(new LoreItemModifier(Lore.of("Deal more damage the more upgrades you have")))
            .build(), new UpgradeCompressorListener()),
    LOTTERY_TICKET(UpgradeData.builder()
            .name("Lottery Ticket")
            .material(Material.PAPER)
            .rarity(ItemRarity.LEGENDARY)
            .addModifier(new LoreItemModifier(Lore.of("You have a higher chance to proc items")))
            .build(), new LotteryTicketListener()),
    //</editor-fold>

    //<editor-fold desc="Mythic Upgrades">
    RABBITS_FOOT(UpgradeData.builder()
            .name("Rabbit's Foot")
            .material(Material.RABBIT_FOOT)
            .rarity(ItemRarity.MYTHIC)
            .addModifier(new LoreItemModifier(Lore.of("Your upgrades will be higher quality")))
            .build(), new RabbitFootListener()),
    UPGRADE_SYNTHESIZER(UpgradeData.builder()
            .name("Item Synthesizer")
            .material(Material.CRAFTING_TABLE)
            .rarity(ItemRarity.MYTHIC)
            .addModifier(new LoreItemModifier(Lore.of("Enemies have a chance to drop items on death")))
            .build(), new UpgradeSynthesizerListener()),
    ;
    //</editor-fold>

    private static final Multimap<ItemRarity, UpgradeType> UPGRADES = LinkedHashMultimap.create();
    static {
        Arrays.stream(values()).sorted(Comparator.comparing(type -> type.data.getRarity())).forEach(type -> UPGRADES.put(type.data.getRarity(), type));
    }
    private static final Object2DoubleLinkedOpenHashMap<ItemRarity> RARITY_WEIGHTS = new Object2DoubleLinkedOpenHashMap<>();
    static {
        RARITY_WEIGHTS.put(ItemRarity.COMMON, 100);     // ~76%
        RARITY_WEIGHTS.put(ItemRarity.RARE, 28);        // ~21%
        RARITY_WEIGHTS.put(ItemRarity.LEGENDARY, 2);    // ~2%
        RARITY_WEIGHTS.put(ItemRarity.MYTHIC, 1);       // ~1%
    }
    private static final WeightedRandom<ItemRarity> WEIGHTED_RANDOM = new WeightedRandom<>(RARITY_WEIGHTS);

    private final UpgradeData data;

    UpgradeType(UpgradeData data) {
        this(data, null);
    }
    UpgradeType(UpgradeData data, UpgradeListener listener) {
        this.data = data;

        if (listener == null) return;
        JavaPlugin.getPlugin(Arcadia.class).getUpgradeEventManager().register(listener, this);
    }

    public UpgradeData getData() {
        return data;
    }

    public static UpgradeType getRandomWeighted() {
        Collection<UpgradeType> upgradeTypes = UPGRADES.get(WEIGHTED_RANDOM.generateRandom());
        return upgradeTypes.stream().skip((int) (Math.random() * upgradeTypes.size())).findFirst().orElseThrow();
    }
}
