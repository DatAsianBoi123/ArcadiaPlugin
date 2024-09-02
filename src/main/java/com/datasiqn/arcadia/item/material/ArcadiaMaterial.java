package com.datasiqn.arcadia.item.material;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaTag;
import com.datasiqn.arcadia.item.ItemRarity;
import com.datasiqn.arcadia.item.abilities.*;
import com.datasiqn.arcadia.item.components.ObsidianKunaiComponent;
import com.datasiqn.arcadia.item.material.data.MaterialData;
import com.datasiqn.arcadia.item.modifiers.LeatherArmorItemModifier;
import com.datasiqn.arcadia.item.modifiers.PotionModifier;
import com.datasiqn.arcadia.item.modifiers.SkullItemModifier;
import com.datasiqn.arcadia.item.type.ItemType;
import com.datasiqn.arcadia.item.type.data.ConsumableData;
import com.datasiqn.arcadia.player.AttributeFormats;
import com.datasiqn.arcadia.player.PlayerAttribute;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ArcadiaMaterial {
    ENCHANTED_STICK(MaterialData.builder(ItemType.NONE)
            .name("Enchanted Stick")
            .material(Material.STICK)
            .enchantGlint(true)
            .build()),
    ANCIENT_CORE(MaterialData.builder(ItemType.NONE)
            .name("Ancient Core")
            .lore(Lore.of("An ancient core taken from the", "heart of a mysterious beast."))
            .material(Material.PLAYER_HEAD)
            .rarity(ItemRarity.LEGENDARY)
            .enchantGlint(true)
            .stackable(false)
            .addModifier(new SkullItemModifier("843968ce4bcc31c3b35e2bcd4a5ac2e98a746b3355e5f8063c323d2ba57ab6e2"))
            .build()),
    ANCIENT_CORE_AWAKENED(MaterialData.builder(ItemType.NONE)
            .name("Awakened Core")
            .lore(Lore.of("A core brought back to its former glory."))
            .material(Material.PLAYER_HEAD)
            .rarity(ItemRarity.LEGENDARY)
            .enchantGlint(true)
            .stackable(false)
            .addModifier(new SkullItemModifier("a4ad229d80308059fa7aed86543779cf933f91b6a437431293d0bb31a0955b71"))
            .build()),
    GUARDIAN_KEY(MaterialData.builder(ItemType.NONE)
            .name("Guardian's Key")
            .material(Material.TRIPWIRE_HOOK)
            .rarity(ItemRarity.RARE)
            .enchantGlint(true)
            .stackable(false)
            .build()),
    SPACE_REWRITER(MaterialData.builder(ItemType.NONE)
            .name("Space Time Rewriter")
            .lore(new LoreBuilder()
                    .append("Combine with an item in an anvil")
                    .append(new ComponentBuilder()
                            .text("to increase its ")
                            .text("Item Quality", ChatColor.DARK_PURPLE)
                            .text(" by ")
                            .percent(0.1)
                            .text(".")
                            .build())
                    .build())
            .material(Material.PLAYER_HEAD)
            .rarity(ItemRarity.MYTHIC)
            .enchantGlint(true)
            .stackable(false)
            .addModifier(new SkullItemModifier("ff379212f42060ae0563c70739a7ec42ad48e70f74210b290d2307a47845ec2c"))
            .build()),
    ESSENCE_OF_BOB(MaterialData.builder(ItemType.NONE)
            .name("Bob's Essence")
            .material(Material.DRAGON_BREATH)
            .rarity(ItemRarity.LEGENDARY)
            .enchantGlint(true)
            .stackable(false)
            .build()),
    ENCHANTED_BOOK(MaterialData.builder(ItemType.NONE)
            .name("Enchanted Book")
            .material(Material.ENCHANTED_BOOK)
            .enchantGlint(true)
            .stackable(false)
            .build()),
    CROOKED_SWORD(MaterialData.builder(ItemType.SWORD)
            .name("Crooked Sword")
            .material(Material.WOODEN_SWORD)
            .damage(3, 5)
            .stackable(false)
            .attribute(PlayerAttribute.DEFENSE, 5)
            .attribute(PlayerAttribute.STRENGTH, 5, 10)
            .addAbility(AbilityType.RIGHT_CLICK, new RunAwayAbility())
            .build()),
    BERSERK_HELMET(MaterialData.builder(ItemType.HELMET)
            .name("Berserker Helmet")
            .material(Material.PLAYER_HEAD)
            .rarity(ItemRarity.MYTHIC)
            .stackable(false)
            .attribute(PlayerAttribute.DEFENSE, 75, 150)
            .attribute(PlayerAttribute.MAX_HEALTH, 400, 800)
            .attribute(PlayerAttribute.STRENGTH, 250, 400)
            .addModifier(new SkullItemModifier("c74f65f9b9958a6392c8b63324d76e80d2b509c1985a00232aecce409585ae2a"))
            .build()),
    BERSERK_CHESTPLATE(MaterialData.builder(ItemType.CHESTPLATE)
            .name("Berserker Chestplate")
            .material(Material.LEATHER_CHESTPLATE)
            .rarity(ItemRarity.MYTHIC)
            .stackable(false)
            .attribute(PlayerAttribute.DEFENSE, 100, 200)
            .attribute(PlayerAttribute.MAX_HEALTH, 500, 1000)
            .attribute(PlayerAttribute.STRENGTH, 300, 500)
            .addModifier(new LeatherArmorItemModifier(Color.fromRGB(0xdb3814)))
            .build()),
    BERSERK_LEGGINGS(MaterialData.builder(ItemType.LEGGINGS)
            .name("Berserker Leggings")
            .material(Material.LEATHER_LEGGINGS)
            .rarity(ItemRarity.MYTHIC)
            .stackable(false)
            .attribute(PlayerAttribute.DEFENSE, 100, 175)
            .attribute(PlayerAttribute.MAX_HEALTH, 450, 900)
            .attribute(PlayerAttribute.STRENGTH, 250, 400)
            .addModifier(new LeatherArmorItemModifier(Color.fromRGB(0xdb3814)))
            .build()),
    BERSERK_BOOTS(MaterialData.builder(ItemType.BOOTS)
            .name("Berserker Boots")
            .material(Material.LEATHER_BOOTS)
            .rarity(ItemRarity.MYTHIC)
            .stackable(false)
            .attribute(PlayerAttribute.DEFENSE, 50, 100)
            .attribute(PlayerAttribute.MAX_HEALTH, 300, 850)
            .attribute(PlayerAttribute.STRENGTH, 200, 400)
            .addModifier(new LeatherArmorItemModifier(Color.fromRGB(0xdb3814)))
            .build()),
    ULTIMATUM(MaterialData.builder(ItemType.SWORD)
            .name(ChatColor.RED + "" + ChatColor.BOLD + "<<" + ChatColor.RED + "Ultimatum" + ChatColor.BOLD + ">>")
            .material(Material.NETHERITE_AXE)
            .rarity(ItemRarity.MYTHIC)
            .damage(1500, 3000)
            .enchantGlint(true)
            .stackable(false)
            .attribute(PlayerAttribute.STRENGTH, 200)
            .addAbility(AbilityType.RIGHT_CLICK, new LastHopeAbility())
            .build()),
    EXCALIBUR(MaterialData.builder(ItemType.SWORD)
            .name("Excalibur")
            .material(Material.GOLDEN_SWORD)
            .rarity(ItemRarity.LEGENDARY)
            .damage(2000, 3500)
            .enchantGlint(true)
            .stackable(false)
            .attribute(PlayerAttribute.DEFENSE, 200)
            .attribute(PlayerAttribute.ATTACK_SPEED, 100)
            .build()),
    HAMMER(MaterialData.builder(ItemType.SWORD)
            .name("War Hammer")
            .material(Material.GOLDEN_AXE)
            .rarity(ItemRarity.LEGENDARY)
            .damage(5000, 7000)
            .enchantGlint(true)
            .stackable(false)
            .attribute(PlayerAttribute.ATTACK_SPEED, -50)
            .build()),
    BOW(MaterialData.builder(ItemType.BOW)
            .name("Bow")
            .material(Material.BOW)
            .damage(5)
            .stackable(false)
            .build()),
    AIR_CANNON(MaterialData.builder(ItemType.NONE)
            .name("Air Cannon")
            .material(Material.DIAMOND_HOE)
            .rarity(ItemRarity.LEGENDARY)
            .enchantGlint(true)
            .stackable(false)
            .addAbility(AbilityType.LEFT_CLICK, new PullAbility())
            .build()),
    OBSIDIAN_KUNAI(MaterialData.builder(ItemType.SWORD)
            .name("Obsidian Kunai")
            .material(Material.NETHERITE_SWORD)
            .rarity(ItemRarity.RARE)
            .damage(800, 1200)
            .enchantGlint(true)
            .stackable(false)
            .attribute(PlayerAttribute.STRENGTH, 60, 75)
            .attribute(PlayerAttribute.SPEED, 0.1)
            .attribute(PlayerAttribute.ATTACK_SPEED, 20)
            .addAbility(AbilityType.RIGHT_CLICK, new MarkAbility(JavaPlugin.getPlugin(Arcadia.class)))
            .addComponent(new ObsidianKunaiComponent())
            .build()),
    STRANGE_JOURNAL(MaterialData.builder(ItemType.NONE)
            .name("Strange Journal")
            .lore(new LoreBuilder()
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
                    .build())
            .material(Material.BOOK)
            .rarity(ItemRarity.SPECIAL)
            .enchantGlint(true)
            .stackable(false)
            .build()),
    CALAMITY_BOX(MaterialData.builder(ItemType.NONE)
            .name("Calamity Box")
            .lore(new LoreBuilder()
                    .append("A strange box with an engraving of a frog")
                    .append("and 3 empty gem slots.")
                    .emptyLine()
                    .append("\"Change can be difficult, but it's how we grow.", ChatColor.GREEN, ChatColor.ITALIC)
                    .append("It can be the hardest thing to realize you can't hold", ChatColor.GREEN, ChatColor.ITALIC)
                    .append("on to something forever. Sometimes, you have to let it go;", ChatColor.GREEN, ChatColor.ITALIC)
                    .append("but, of the things you let go, you'd be surprised what", ChatColor.GREEN, ChatColor.ITALIC)
                    .append("makes its way back to you.\"", ChatColor.GREEN, ChatColor.ITALIC)
                    .build())
            .material(Material.CHEST)
            .rarity(ItemRarity.SPECIAL)
            .enchantGlint(true)
            .stackable(false)
            .build()),
    TITAN_BLOOD(MaterialData.builder(ItemType.NONE)
            .name("Titan's Blood")
            .lore(new LoreBuilder()
                    .append("A vial filled with a strange blue liquid.")
                    .emptyLine()
                    .append("\"Look, kid, everyone wants to believe they are 'chosen'.", ChatColor.GOLD, ChatColor.ITALIC)
                    .append("But if we all waited around for a prophecy to make us special,", ChatColor.GOLD, ChatColor.ITALIC)
                    .append("we'd die waiting. And that's why you need to choose yourself.\"", ChatColor.GOLD, ChatColor.ITALIC)
                    .build())
            .material(Material.POTION)
            .rarity(ItemRarity.SPECIAL)
            .enchantGlint(true)
            .stackable(false)
            .addModifier(new PotionModifier(Color.BLUE))
            .build()),
    PURE_NAIL(MaterialData.builder(ItemType.NAIL)
            .name("Pure Nail")
            .lore(new LoreBuilder()
                    .append("You can see a faint pail light emanating from this weapon.")
                    .append("Upon closer inspection, you conclude that this is way too")
                    .append("big to be considered a nail.")
                    .emptyLine()
                    .append("NO MIND TO THINK.", ChatColor.DARK_GRAY, ChatColor.ITALIC)
                    .append("NO WILL TO BREAK.", ChatColor.DARK_GRAY, ChatColor.ITALIC)
                    .append("NO VOICE TO CRY SUFFERING.", ChatColor.DARK_GRAY, ChatColor.ITALIC)
                    .append("BORN OF GOD AND VOID.", ChatColor.DARK_GRAY, ChatColor.ITALIC)
                    .build())
            .material(Material.IRON_SWORD)
            .rarity(ItemRarity.SPECIAL)
            .damage(1000, 1500)
            .enchantGlint(true)
            .stackable(false)
            .attribute(PlayerAttribute.STRENGTH, 50, 100)
            .build()),
    ANCIENT_TRANSLATOR(MaterialData.builder(ItemType.NONE)
            .name("Ancient Translator")
            .lore(new LoreBuilder()
                    .append("An ancient translator from a previous universe.")
                    .append("It is unclear what language it translates from.")
                    .emptyLine()
                    .append("\"It’s tempting to linger in this moment, while every", ChatColor.WHITE, ChatColor.ITALIC)
                    .append("possibility still exists. But unless they are collapsed", ChatColor.WHITE, ChatColor.ITALIC)
                    .append("by an observer, they will never be more than possibilities.\"", ChatColor.WHITE, ChatColor.ITALIC)
                    .build())
            .material(Material.IRON_AXE)
            .rarity(ItemRarity.SPECIAL)
            .stackable(false)
            .build()),
    BROKEN_LIGHTBULB(MaterialData.builder(ItemType.NONE)
            .name("Broken Lightbulb")
            .lore(new LoreBuilder()
                    .append("The remnants of a once working lightbulb.")
                    .append("There is a small label that reads, 'SUN'.")
                    .emptyLine()
                    .append("[...This world would only exist as a memory]", ChatColor.LIGHT_PURPLE, ChatColor.ITALIC)
                    .append(new ComponentBuilder()
                            .text("[For ", ChatColor.LIGHT_PURPLE, ChatColor.ITALIC)
                            .text("----", ChatColor.GRAY, ChatColor.MAGIC)
                            .text(", a dream.]", ChatColor.LIGHT_PURPLE, ChatColor.ITALIC)
                            .build())
                    .append("[Like thousands of other dreams.]", ChatColor.LIGHT_PURPLE, ChatColor.ITALIC)
                    .append("[For you, a story.]", ChatColor.LIGHT_PURPLE, ChatColor.ITALIC)
                    .append("[Like thousands of other stories.]", ChatColor.LIGHT_PURPLE, ChatColor.ITALIC)
                    .build())
            .material(Material.PLAYER_HEAD)
            .rarity(ItemRarity.SPECIAL)
            .stackable(false)
            .addModifier(new SkullItemModifier("5381a2d5af55779c4dd39f403f982ac8a9ead269ca4e80501e1dcb8631c5a290"))
            .build()),
    PHOTO_ALBUM(MaterialData.builder(ItemType.NONE)
            .name("Photo Album")
            .lore(new LoreBuilder()
                    .append("Contains memories from a simpler time.")
                    .emptyLine()
                    .append("Don't worry...", ChatColor.WHITE, ChatColor.ITALIC)
                    .append("Everything is going to be okay...", ChatColor.WHITE, ChatColor.ITALIC)
                    .append("No matter what happens...", ChatColor.WHITE, ChatColor.ITALIC)
                    .append("Promise me we’ll always be there for each other...", ChatColor.WHITE, ChatColor.ITALIC)
                    .append("Promise me...", ChatColor.WHITE, ChatColor.ITALIC)
                    .build())
            .material(Material.WRITTEN_BOOK)
            .rarity(ItemRarity.SPECIAL)
            .stackable(false)
            .build()),

    FRUITY_APPLE(MaterialData.builder(ItemType.CONSUMABLE,
            new ConsumableData(new LoreBuilder()
                    .append(new ComponentBuilder()
                            .text("Eating this heals you by ")
                            .stat(AttributeFormats.HEALTH, 10)
                            .build())
                    .build(),
                    10,
                    player -> player.heal(10)))
            .name("Fruity Apple")
            .material(Material.APPLE)
            .build()),
    ;

    private final MaterialData<?> data;

    ArcadiaMaterial(MaterialData<?> data) {
        this.data = data;
    }

    @NotNull
    public MaterialData<?> getData() {
        return data;
    }

    public static @Nullable ArcadiaMaterial fromItemStack(@NotNull ItemStack itemStack) {
        try {
            return valueOf(itemStack.getType().name());
        } catch (IllegalArgumentException ignored) { }

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return null;

        return fromPdc(meta.getPersistentDataContainer());
    }
    public static @Nullable ArcadiaMaterial fromPdc(@NotNull PersistentDataContainer pdc) {
        if (!PdcUtil.has(pdc, ArcadiaTag.ITEM_ID)) return null;
        if (PdcUtil.getOrDefault(pdc, ArcadiaTag.ITEM_MATERIAL, false)) return null;

        return PdcUtil.get(pdc, ArcadiaTag.ITEM_ID).getMaterial();
    }
}
