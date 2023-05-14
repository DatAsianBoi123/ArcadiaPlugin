package com.datasiqn.arcadia.items.materials;

import com.datasiqn.arcadia.items.ItemRarity;
import com.datasiqn.arcadia.items.abilities.AbilityType;
import com.datasiqn.arcadia.items.abilities.ItemAbility;
import com.datasiqn.arcadia.items.materials.data.MaterialData;
import com.datasiqn.arcadia.items.meta.ArcadiaItemMeta;
import com.datasiqn.arcadia.items.modifiers.LeatherArmorItemModifier;
import com.datasiqn.arcadia.items.modifiers.LoreItemModifier;
import com.datasiqn.arcadia.items.modifiers.SkullItemModifier;
import com.datasiqn.arcadia.items.stats.AttributeRange;
import com.datasiqn.arcadia.items.stats.ItemAttribute;
import com.datasiqn.arcadia.items.stats.ItemStats;
import com.datasiqn.arcadia.items.type.ItemType;
import com.datasiqn.arcadia.items.type.data.ConsumableData;
import com.datasiqn.arcadia.players.ArcadiaSender;
import com.datasiqn.arcadia.players.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
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
            .addModifier(new LoreItemModifier("An ancient core taken from the", "heart of a mysterious beast."))
            .build()),
    ANCIENT_CORE_AWAKENED(new MaterialData.Builder<>(ItemType.NONE)
            .name("Awakened Core")
            .material(Material.PLAYER_HEAD)
            .rarity(ItemRarity.LEGENDARY)
            .enchantGlint(true)
            .stackable(false)
            .addModifier(new SkullItemModifier("a4ad229d80308059fa7aed86543779cf933f91b6a437431293d0bb31a0955b71"))
            .addModifier(new LoreItemModifier("A core brought back to its former glory."))
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
            .addModifier(new LoreItemModifier("Combine with an item in an anvil", "to increase its " + ChatColor.DARK_PURPLE + "Item Quality" + ChatColor.GRAY + " by " + ChatColor.BLUE + "10%"))
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
            .itemAbility(new ItemAbility("Run Away", Collections.singletonList("Gives you speed"), AbilityType.RIGHT_CLICK, 60, executor -> {
                Player player = executor.playerData().getPlayer();
                player.playSound(player, Sound.BLOCK_AMETHYST_BLOCK_PLACE, 1, 1);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 1));
            }))
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
            .itemAbility(new ItemAbility("Last Hope", Collections.singletonList("Does cool things"), AbilityType.RIGHT_CLICK, 4000, executor -> {
                PlayerData playerData = executor.playerData();
                ArcadiaSender<Player> player = executor.playerData().getSender();

                playerData.heal();
                playerData.updateValues();
                playerData.updateActionbar();
                player.get().getWorld().createExplosion(player.get().getLocation(), 8, false, false, player.get());
            }))
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
    FRUITY_APPLE(new MaterialData.Builder<>(ItemType.CONSUMABLE,
            new ConsumableData(Collections.singletonList("Eating this heals you by " + ItemAttribute.HEALTH.getColor() + "10" + ItemAttribute.HEALTH.getIcon() + ChatColor.GRAY + "."),
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
