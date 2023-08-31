package com.datasiqn.arcadia.item.material;

import com.datasiqn.arcadia.ArcadiaTag;
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
import com.datasiqn.arcadia.util.PdcUtil;
import com.datasiqn.arcadia.util.lorebuilder.Lore;
import com.datasiqn.arcadia.util.lorebuilder.LoreBuilder;
import com.datasiqn.arcadia.util.lorebuilder.component.ComponentBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

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
    }),
    BOW(new MaterialData.Builder<>(ItemType.BOW)
            .name("Bow")
            .material(Material.BOW)
            .stackable(false)
            .build(), meta -> {
        ItemStats itemStats = meta.getItemStats();
        itemStats.setAttribute(ItemAttribute.DAMAGE, 5);
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
                    .append("\"If you've ever taken a road trip through the pacific", ChatColor.YELLOW, ChatColor.ITALIC)
                    .append("northwest, you've probably seen a bumper sticker for a", ChatColor.YELLOW, ChatColor.ITALIC)
                    .append("place called \"Gravity Falls\". It's not on any maps,", ChatColor.YELLOW, ChatColor.ITALIC)
                    .append("and most people have never heard of it, some people", ChatColor.YELLOW, ChatColor.ITALIC)
                    .append("think it's a myth. But if you're curious, don't wait.", ChatColor.YELLOW, ChatColor.ITALIC)
                    .append("Take a trip. Find it. It's out there somewhere in the woods.", ChatColor.YELLOW, ChatColor.ITALIC)
                    .append("Waiting.\"", ChatColor.YELLOW, ChatColor.ITALIC)
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
                    .append("It can be the hardest thing to realize you can't hold", ChatColor.GREEN, ChatColor.ITALIC)
                    .append("on to something forever. Sometimes, you have to let it go;", ChatColor.GREEN, ChatColor.ITALIC)
                    .append("but, of the things you let go, you'd be surprised what", ChatColor.GREEN, ChatColor.ITALIC)
                    .append("makes its way back to you.\"", ChatColor.GREEN, ChatColor.ITALIC)
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
                    .append("\"Look, kid, everyone wants to believe they are 'chosen'.", ChatColor.GOLD, ChatColor.ITALIC)
                    .append("But if we all waited around for a prophecy to make us special,", ChatColor.GOLD, ChatColor.ITALIC)
                    .append("we'd die waiting. And that's why you need to choose yourself.\"", ChatColor.GOLD, ChatColor.ITALIC)
                    .build()))
            .build()),
    PURE_NAIL(new MaterialData.Builder<>(ItemType.NAIL)
            .name("Pure Nail")
            .material(Material.IRON_SWORD)
            .rarity(ItemRarity.SPECIAL)
            .enchantGlint(true)
            .stackable(false)
            .addModifier(new LoreItemModifier(new LoreBuilder()
                    .append("You can see a faint pail light emanating from this weapon.")
                    .append("Upon closer inspection, you conclude that this is way too")
                    .append("big to be considered a nail.")
                    .emptyLine()
                    .append("NO MIND TO THINK.", ChatColor.DARK_GRAY, ChatColor.ITALIC)
                    .append("NO WILL TO BREAK.", ChatColor.DARK_GRAY, ChatColor.ITALIC)
                    .append("NO VOICE TO CRY SUFFERING.", ChatColor.DARK_GRAY, ChatColor.ITALIC)
                    .append("BORN OF GOD AND VOID.", ChatColor.DARK_GRAY, ChatColor.ITALIC)
                    .build()))
            .build(), meta -> {
        ItemStats itemStats = meta.getItemStats();
        itemStats.setAttribute(ItemAttribute.DAMAGE, new AttributeRange(1000, 1500));
        itemStats.setAttribute(ItemAttribute.STRENGTH, new AttributeRange(50, 100));
    }),

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
            .build(), meta -> meta.getItemStats().setAttribute(ItemAttribute.STRENGTH, 1)),
    ;

    private final MaterialData<?> data;
    private final Consumer<ArcadiaItemMeta> metaBuilder;

    ArcadiaMaterial(MaterialData<?> data) {
        this(data, meta -> {});
    }
    ArcadiaMaterial(MaterialData<?> data, Consumer<ArcadiaItemMeta> metaBuilder) {
        this.data = data;
        this.metaBuilder = metaBuilder;
    }

    @NotNull
    public MaterialData<?> getData() {
        return data;
    }

    @NotNull
    public ArcadiaItemMeta createItemMeta(UUID uuid) {
        ArcadiaItemMeta meta = new ArcadiaItemMeta(uuid);
        metaBuilder.accept(meta);
        return meta;
    }

    public static @Nullable ArcadiaMaterial fromItemStack(@NotNull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return null;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (!PdcUtil.has(pdc, ArcadiaTag.ITEM_ID)) return null;
        if (PdcUtil.getOrDefault(pdc, ArcadiaTag.ITEM_MATERIAL, false)) return null;

        return PdcUtil.get(pdc, ArcadiaTag.ITEM_ID).getMaterial();
    }
}
