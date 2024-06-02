package com.datasiqn.arcadia.upgrade;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.item.ItemRarity;
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
            .lore(Lore.of("Killing enemies heal you"))
            .material(Material.POTION)
            .rarity(ItemRarity.COMMON)
            .addModifier(new PotionModifier(Color.RED))
            .build(), new BloodChaliceListener()),
    SNEAKERS(UpgradeData.builder()
            .name("Sneakers")
            .lore(Lore.of("Increases movement speed"))
            .material(Material.LEATHER_BOOTS)
            .rarity(ItemRarity.COMMON)
            .build(), new SneakerListener()),
    COFFEE(UpgradeData.builder()
            .name("Coffee")
            .lore(Lore.of("Slightly increases attack speed"))
            .material(Material.POTION)
            .rarity(ItemRarity.COMMON)
            .addModifier(new PotionModifier(Color.fromRGB(63, 29, 11)))
            .build(), new CoffeeListener()),
    FLAMETHROWER(UpgradeData.builder()
            .name("Flamethrower")
            .lore(Lore.of("Chance on hit to light the enemy on fire"))
            .material(Material.FLINT_AND_STEEL)
            .rarity(ItemRarity.COMMON)
            .build(), new FlamethrowerListener()),
    //</editor-fold>

    //<editor-fold desc="Rare Upgrades">
    MAGIC_QUIVER(UpgradeData.builder()
            .name("Magic Quiver")
            .lore(Lore.of("Bows shoot extra arrows"))
            .material(Material.LEATHER_HORSE_ARMOR)
            .rarity(ItemRarity.RARE)
            .build(), new MagicQuiverListener()),
    LIGHTNING_BOTTLE(UpgradeData.builder()
            .name("Lightning in a Bottle")
            .lore(Lore.of("Chance on hit to strike lightning"))
            .material(Material.POTION)
            .rarity(ItemRarity.RARE)
            .addModifier(new PotionModifier(Color.BLUE))
            .build(), new LightningBottleListener()),
    ROLLER_SKATES(UpgradeData.builder()
            .name("Roller Skates")
            .lore(Lore.of("Hitting an enemy while sprinting deals extra damage"))
            .material(Material.IRON_BOOTS)
            .rarity(ItemRarity.RARE)
            .build(), new RollerSkatesListener()),
    //</editor-fold>

    //<editor-fold desc="Legendary Upgrades">
    UPGRADE_COMPRESSOR(UpgradeData.builder()
            .name("Upgrade Compressor")
            .lore(Lore.of("Deal more damage the more upgrades you have"))
            .material(Material.PISTON)
            .rarity(ItemRarity.LEGENDARY)
            .build(), new UpgradeCompressorListener()),
    LOTTERY_TICKET(UpgradeData.builder()
            .name("Lottery Ticket")
            .lore(Lore.of("You have a higher chance to proc items"))
            .material(Material.PAPER)
            .rarity(ItemRarity.LEGENDARY)
            .build(), new LotteryTicketListener()),
    //</editor-fold>

    //<editor-fold desc="Mythic Upgrades">
    RABBITS_FOOT(UpgradeData.builder()
            .name("Rabbit's Foot")
            .lore(Lore.of("Your upgrades will be higher quality"))
            .material(Material.RABBIT_FOOT)
            .rarity(ItemRarity.MYTHIC)
            .build(), new RabbitFootListener()),
    UPGRADE_SYNTHESIZER(UpgradeData.builder()
            .name("Item Synthesizer")
            .lore(Lore.of("Enemies have a chance to drop items on death"))
            .material(Material.CRAFTING_TABLE)
            .rarity(ItemRarity.MYTHIC)
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
