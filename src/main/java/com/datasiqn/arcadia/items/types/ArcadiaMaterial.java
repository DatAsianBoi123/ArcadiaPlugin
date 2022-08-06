package com.datasiqn.arcadia.items.types;

import com.datasiqn.arcadia.items.ItemRarity;
import com.datasiqn.arcadia.items.data.ItemData;
import com.datasiqn.arcadia.items.meta.ArcadiaItemMeta;
import com.datasiqn.arcadia.items.meta.MetaBuilder;
import com.datasiqn.arcadia.items.modifiers.LoreItemModifier;
import com.datasiqn.arcadia.items.modifiers.SkullItemModifier;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@SuppressWarnings("unused")
public enum ArcadiaMaterial {
    ENCHANTED_STICK(new ItemData("Enchanted Stick", "ENCHANTED_STICK", Material.STICK, ItemRarity.COMMON, true, true)),
    ANCIENT_CORE(new ItemData("Ancient Core", "ANCIENT_CORE", Material.PLAYER_HEAD, ItemRarity.LEGENDARY, true, false)
            .addItemModifier(new SkullItemModifier("843968ce4bcc31c3b35e2bcd4a5ac2e98a746b3355e5f8063c323d2ba57ab6e2"))
            .addItemModifier(new LoreItemModifier("An ancient core taken from the", "heart of a mysterious beast."))),
    ANCIENT_CORE_AWAKENED(new ItemData("Awakened Core", "ANCIENT_CORE_AWAKENED", Material.PLAYER_HEAD, ItemRarity.LEGENDARY, true, false)
            .addItemModifier(new SkullItemModifier("a4ad229d80308059fa7aed86543779cf933f91b6a437431293d0bb31a0955b71"))
            .addItemModifier(new LoreItemModifier("A core brought back to its former glory."))),
    GUARDIAN_KEY(new ItemData("Guardian's Key", "GUARDIAN_KEY", Material.TRIPWIRE_HOOK, ItemRarity.RARE, true, false)),
    SPACE_REWRITER(new ItemData("Space Time Rewriter", "SPACE_REWRITER", Material.PLAYER_HEAD, ItemRarity.MYTHIC, true, false)
            .addItemModifier(new LoreItemModifier("Combine with an item in an anvil",
                    "to increase its " + ChatColor.DARK_PURPLE + "Item Quality" + ChatColor.GRAY + " by " + ChatColor.BLUE + "10%"))
            .addItemModifier(new SkullItemModifier("ff379212f42060ae0563c70739a7ec42ad48e70f74210b290d2307a47845ec2c"))),
    ESSENCE_OF_BOB(new ItemData("Bob's Essence", "ESSENCE_OF_BOB", Material.DRAGON_BREATH, ItemRarity.LEGENDARY, true, false)),
    ENCHANTED_BOOK(new ItemData("Enchanted Book", "ENCHANTED_BOOK", Material.ENCHANTED_BOOK, ItemRarity.COMMON, true, false)),
    CROOKED_SWORD(new ItemCrookedSword()),
    BERSERK_HELMET(new ItemBerserkHelmet()),
    BERSERK_CHESTPLATE(new ItemBerserkChestplate()),
    BERSERK_LEGGINGS(new ItemBerserkLeggings()),
    BERSERK_BOOTS(new ItemBerserkBoots()),
    ULTIMATUM(new ItemUltimatum()),
    EXCALIBUR(new ItemExcalibur()),
    BOW(new ItemBow());

    private final ItemData data;
    private final MetaBuilder metaBuilder;

    ArcadiaMaterial(ItemData itemData) {
        this(itemData, new MetaBuilder());
    }
    ArcadiaMaterial(@NotNull CustomMaterial type) {
        this(type.getItemData(), type.getMetaBuilder());
    }
    ArcadiaMaterial(ItemData itemData, MetaBuilder metaBuilder) {
        this.data = itemData;
        this.metaBuilder = metaBuilder;
    }

    @NotNull
    public ItemData getItemData() {
        return data;
    }

    @NotNull
    public ArcadiaItemMeta createItemMeta(UUID uuid) {
        return metaBuilder.build(uuid);
    }
}
