package com.datasiqn.arcadia.item.material;

import com.datasiqn.arcadia.item.ItemRarity;
import com.datasiqn.arcadia.item.abilities.*;
import com.datasiqn.arcadia.item.material.data.MaterialData;
import com.datasiqn.arcadia.item.meta.ArcadiaItemMeta;
import com.datasiqn.arcadia.item.modifiers.LeatherArmorItemModifier;
import com.datasiqn.arcadia.item.modifiers.LoreItemModifier;
import com.datasiqn.arcadia.item.modifiers.PotionModifier;
import com.datasiqn.arcadia.item.modifiers.SkullItemModifier;
import com.datasiqn.arcadia.item.stat.AttributeRange;
import com.datasiqn.arcadia.item.stat.ItemAttribute;
import com.datasiqn.arcadia.item.stat.ItemStats;
import com.datasiqn.arcadia.item.type.ItemType;
import com.datasiqn.arcadia.item.type.data.ConsumableData;
import com.datasiqn.arcadia.util.lorebuilder.Lore;
import com.datasiqn.arcadia.util.lorebuilder.LoreBuilder;
import com.datasiqn.arcadia.util.lorebuilder.component.ComponentBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.UnaryOperator;

public enum ArcadiaMaterial {
    ENCHANTED_STICK(new MaterialData.Builder<>(ItemType.NONE)
            .name("Enchanted Stick")
            .material(Material.STICK)
            .enchantGlint(true)
            .build()),
    ANCIENT_CORE(new MaterialData.Builder<>(ItemType.NONE)
            .name("Ancient Core")
            .material(Material.PLAYER_HEAD)
            .rarity(ItemRarity.LEGENDARY)
            .enchantGlint(true)
            .stackable(false)
            .addModifier(new SkullItemModifier("843968ce4bcc31c3b35e2bcd4a5ac2e98a746b3355e5f8063c323d2ba57ab6e2"))
            .addModifier(new LoreItemModifier(Lore.of("An ancient core taken from the", "heart of a mysterious beast.")))
            .build()),
    ANCIENT_CORE_AWAKENED(new MaterialData.Builder<>(ItemType.NONE)
            .name("Awakened Core")
            .material(Material.PLAYER_HEAD)
            .rarity(ItemRarity.LEGENDARY)
            .enchantGlint(true)
            .stackable(false)
            .addModifier(new SkullItemModifier("a4ad229d80308059fa7aed86543779cf933f91b6a437431293d0bb31a0955b71"))
            .addModifier(new LoreItemModifier(Lore.of("A core brought back to its former glory.")))
            .build()),
    GUARDIAN_KEY(new MaterialData.Builder<>(ItemType.NONE)
            .name("Guardian's Key")
            .material(Material.TRIPWIRE_HOOK)
            .rarity(ItemRarity.RARE)
            .enchantGlint(true)
            .stackable(false)
            .build()),
    SPACE_REWRITER(new MaterialData.Builder<>(ItemType.NONE)
            .name("Space Time Rewriter")
            .material(Material.PLAYER_HEAD)
            .rarity(ItemRarity.MYTHIC)
            .enchantGlint(true)
            .stackable(false)
            .addModifier(new SkullItemModifier("ff379212f42060ae0563c70739a7ec42ad48e70f74210b290d2307a47845ec2c"))
            .addModifier(new LoreItemModifier(new LoreBuilder()
                    .append("Combine with an item in an anvil")
                    .append(new ComponentBuilder()
                            .text("to increase its ")
                            .text("Item Quality", ChatColor.DARK_PURPLE)
                            .text(" by ")
                            .percent(0.1)
                            .text(".")
                            .build())
                    .build()))
            .build()),
    ESSENCE_OF_BOB(new MaterialData.Builder<>(ItemType.NONE)
            .name("Bob's Essence")
            .material(Material.DRAGON_BREATH)
            .rarity(ItemRarity.LEGENDARY)
            .enchantGlint(true)
            .stackable(false)
            .build()),
    ENCHANTED_BOOK(new MaterialData.Builder<>(ItemType.NONE)
            .name("Enchanted Book")
            .material(Material.ENCHANTED_BOOK)
            .enchantGlint(true)
            .stackable(false)
            .build()),
    CROOKED_SWORD(new MaterialData.Builder<>(ItemType.SWORD)
            .name("Crooked Sword")
            .material(Material.WOODEN_SWORD)
            .stackable(false)
            .addAbility(AbilityType.RIGHT_CLICK, new RunAwayAbility())
            .build(), meta -> {
        ItemStats itemStats = meta.getItemStats();
        itemStats.setAttribute(ItemAttribute.DAMAGE, new AttributeRange(3, 5));
        itemStats.setAttribute(ItemAttribute.DEFENSE, 5);
        itemStats.setAttribute(ItemAttribute.STRENGTH, new AttributeRange(5, 10));
        return meta;
    }),
    BERSERK_HELMET(new MaterialData.Builder<>(ItemType.HELMET)
            .name("Berserker Helmet")
            .material(Material.PLAYER_HEAD)
            .rarity(ItemRarity.MYTHIC)
            .stackable(false)
            .addModifier(new SkullItemModifier("c74f65f9b9958a6392c8b63324d76e80d2b509c1985a00232aecce409585ae2a"))
            .build(), meta -> {
        ItemStats itemStats = meta.getItemStats();
        itemStats.setAttribute(ItemAttribute.DEFENSE, new AttributeRange(75, 150));
        itemStats.setAttribute(ItemAttribute.HEALTH, new AttributeRange(400, 800));
        itemStats.setAttribute(ItemAttribute.STRENGTH, new AttributeRange(250, 400));
        return meta;
    }),
    BERSERK_CHESTPLATE(new MaterialData.Builder<>(ItemType.CHESTPLATE)
            .name("Berserker Chestplate")
            .material(Material.LEATHER_CHESTPLATE)
            .rarity(ItemRarity.MYTHIC)
            .stackable(false)
            .addModifier(new LeatherArmorItemModifier(Color.fromRGB(0xdb3814)))
            .build(), meta -> {
        ItemStats itemStats = meta.getItemStats();
        itemStats.setAttribute(ItemAttribute.DEFENSE, new AttributeRange(100d, 200d));
        itemStats.setAttribute(ItemAttribute.HEALTH, new AttributeRange(500d, 1000d));
        itemStats.setAttribute(ItemAttribute.STRENGTH, new AttributeRange(300d, 500d));
        return meta;
    }),
    BERSERK_LEGGINGS(new MaterialData.Builder<>(ItemType.LEGGINGS)
            .name("Berserker Leggings")
            .material(Material.LEATHER_LEGGINGS)
            .rarity(ItemRarity.MYTHIC)
            .stackable(false)
            .addModifier(new LeatherArmorItemModifier(Color.fromRGB(0xdb3814)))
            .build(), meta -> {
        ItemStats itemStats = meta.getItemStats();
        itemStats.setAttribute(ItemAttribute.DEFENSE, new AttributeRange(100, 175));
        itemStats.setAttribute(ItemAttribute.HEALTH, new AttributeRange(450, 900));
        itemStats.setAttribute(ItemAttribute.STRENGTH, new AttributeRange(250, 400));
        return meta;
    }),
    BERSERK_BOOTS(new MaterialData.Builder<>(ItemType.BOOTS)
            .name("Berserker Boots")
            .material(Material.LEATHER_BOOTS)
            .rarity(ItemRarity.MYTHIC)
            .stackable(false)
            .addModifier(new LeatherArmorItemModifier(Color.fromRGB(0xdb3814)))
            .build(), meta -> {
        ItemStats itemStats = meta.getItemStats();
        itemStats.setAttribute(ItemAttribute.DEFENSE, new AttributeRange(50d, 100d));
        itemStats.setAttribute(ItemAttribute.HEALTH, new AttributeRange(300d, 850d));
        itemStats.setAttribute(ItemAttribute.STRENGTH, new AttributeRange(200d, 400d));
        return meta;
    }),
    ULTIMATUM(new MaterialData.Builder<>(ItemType.SWORD)
            .name(ChatColor.RED + "" + ChatColor.BOLD + "<<" + ChatColor.RED + "Ultimatum" + ChatColor.BOLD + ">>")
            .material(Material.NETHERITE_AXE)
            .rarity(ItemRarity.MYTHIC)
            .enchantGlint(true)
            .stackable(false)
            .addAbility(AbilityType.RIGHT_CLICK, new LastHopeAbility())
            .build(), meta -> {
        ItemStats itemStats = meta.getItemStats();
        itemStats.setAttribute(ItemAttribute.DAMAGE, new AttributeRange(1500, 3000));
        itemStats.setAttribute(ItemAttribute.STRENGTH, 200);
        return meta;
    }),
    EXCALIBUR(new MaterialData.Builder<>(ItemType.SWORD)
            .name("Excalibur")
            .material(Material.GOLDEN_SWORD)
            .rarity(ItemRarity.LEGENDARY)
            .enchantGlint(true)
            .stackable(false)
            .build(), meta -> {
        ItemStats itemStats = meta.getItemStats();
        itemStats.setAttribute(ItemAttribute.DAMAGE, new AttributeRange(2000, 3500));
        itemStats.setAttribute(ItemAttribute.DEFENSE, 200);
        itemStats.setAttribute(ItemAttribute.ATTACK_SPEED, 100);
        return meta;
    }),
    HAMMER(new MaterialData.Builder<>(ItemType.SWORD)
            .name("War Hammer")
            .material(Material.GOLDEN_AXE)
            .rarity(ItemRarity.LEGENDARY)
            .enchantGlint(true)
            .stackable(false)
            .build(), meta -> {
        ItemStats itemStats = meta.getItemStats();
        itemStats.setAttribute(ItemAttribute.DAMAGE, new AttributeRange(5000, 7000));
        itemStats.setAttribute(ItemAttribute.ATTACK_SPEED, -50);
        return meta;
    }),
    BOW(new MaterialData.Builder<>(ItemType.BOW)
            .name("Bow")
            .material(Material.BOW)
            .stackable(false)
            .build(), meta -> {
        ItemStats itemStats = meta.getItemStats();
        itemStats.setAttribute(ItemAttribute.DAMAGE, 5);
        return meta;
    }),
    AIR_CANNON(new MaterialData.Builder<>(ItemType.NONE)
            .name("Air Cannon")
            .material(Material.DIAMOND_HOE)
            .rarity(ItemRarity.LEGENDARY)
            .enchantGlint(true)
            .stackable(false)
            .addAbility(AbilityType.LEFT_CLICK, new PullAbility())
            .build()),
    TEST_ITEM(new MaterialData.Builder<>(ItemType.SWORD)
            .name("Test Sword")
            .material(Material.NETHERITE_SWORD)
            .rarity(ItemRarity.MYTHIC)
            .enchantGlint(true)
            .stackable(false)
            .addAbility(AbilityType.RIGHT_CLICK, new MarkAbility())
            .build()),
    STRANGE_JOURNAL(new MaterialData.Builder<>(ItemType.NONE)
            .name("Strange Journal")
            .material(Material.BOOK)
            .rarity(ItemRarity.SPECIAL)
            .enchantGlint(true)
            .stackable(false)
            .addModifier(new LoreItemModifier(new LoreBuilder()
                    .append("A strange journal with a large 3 on a")
                    .append("6 fingered hand imprint.")
                    .emptyLine()
                    .append("\"If you've ever taken a road trip through the pacific northwest,", ChatColor.YELLOW, ChatColor.ITALIC)
                    .append("you've probably seen a bumper sticker for a place called", ChatColor.YELLOW, ChatColor.ITALIC)
                    .append("\"Gravity Falls\". It's not on any maps, and most people", ChatColor.YELLOW, ChatColor.ITALIC)
                    .append("have never heard of it, some people think it's a myth.", ChatColor.YELLOW, ChatColor.ITALIC)
                    .append("But if you're curious, don't wait. Take a trip.", ChatColor.YELLOW, ChatColor.ITALIC)
                    .append("Find it. It's out there somewhere in the woods. Waiting.\"", ChatColor.YELLOW, ChatColor.ITALIC)
                    .build()))
            .build()),
    CALAMITY_BOX(new MaterialData.Builder<>(ItemType.NONE)
            .name("Calamity Box")
            .material(Material.CHEST)
            .rarity(ItemRarity.SPECIAL)
            .enchantGlint(true)
            .stackable(false)
            .addModifier(new LoreItemModifier(new LoreBuilder()
                    .append("A strange box with an engraving of a frog")
                    .append("and 3 empty gem slots.")
                    .emptyLine()
                    .append("\"Change can be difficult, but it's how we grow.", ChatColor.GREEN, ChatColor.ITALIC)
                    .append("It can be the hardest thing to realize you can't hold on to", ChatColor.GREEN, ChatColor.ITALIC)
                    .append("something forever. Sometimes, you have to let it go; but, of", ChatColor.GREEN, ChatColor.ITALIC)
                    .append("the things you let go, you'd be surprised what makes", ChatColor.GREEN, ChatColor.ITALIC)
                    .append("its way back to you.\"", ChatColor.GREEN, ChatColor.ITALIC)
                    .build()))
            .build()),
    TITAN_BLOOD(new MaterialData.Builder<>(ItemType.NONE)
            .name("Titan's Blood")
            .material(Material.POTION)
            .rarity(ItemRarity.SPECIAL)
            .enchantGlint(true)
            .stackable(false)
            .addModifier(new PotionModifier(Color.BLUE))
            .addModifier(new LoreItemModifier(new LoreBuilder()
                    .append("A vial filled with a strange blue liquid.")
                    .emptyLine()
                    .append("\"Look, kid, everyone wants to believe they are \"chosen\".", ChatColor.GOLD, ChatColor.ITALIC)
                    .append("But if we all waited around for a prophecy to make us special,", ChatColor.GOLD, ChatColor.ITALIC)
                    .append("we'd die waiting. And that's why you need to choose yourself.\"", ChatColor.GOLD, ChatColor.ITALIC)
                    .build()))
            .build()),

    FRUITY_APPLE(new MaterialData.Builder<>(ItemType.CONSUMABLE,
            new ConsumableData(new LoreBuilder()
                    .append(new ComponentBuilder()
                            .text("Eating this heals you by ")
                            .stat(10, ItemAttribute.HEALTH)
                            .build())
                    .build(),
                    10,
                    data -> data.heal(10)))
            .name("Fruity Apple")
            .material(Material.APPLE)
            .build()),

    STRENGTH_STONE(new MaterialData.Builder<>(ItemType.POWER_STONE)
            .name("Strength Stone")
            .material(Material.REDSTONE)
            .stackable(false)
            .build(), meta -> {
        meta.getItemStats().setAttribute(ItemAttribute.STRENGTH, 1);
        return meta;
    }),
    ;

    private final MaterialData<?> data;
    private final UnaryOperator<ArcadiaItemMeta> metaBuilder;

    ArcadiaMaterial(MaterialData<?> data) {
        this(data, meta -> meta);
    }
    ArcadiaMaterial(MaterialData<?> data, UnaryOperator<ArcadiaItemMeta> metaBuilder) {
        this.data = data;
        this.metaBuilder = metaBuilder;
    }

    @NotNull
    public MaterialData<?> getData() {
        return data;
    }

    @NotNull
    public ArcadiaItemMeta createItemMeta(UUID uuid) {
        return metaBuilder.apply(new ArcadiaItemMeta(uuid));
    }
}
