package com.datasiqn.arcadia.items;

import com.datasiqn.arcadia.ArcadiaKeys;
import com.datasiqn.arcadia.datatypes.EnchantsDataType;
import com.datasiqn.arcadia.items.data.ItemData;
import com.datasiqn.arcadia.items.data.MaterialItemData;
import com.datasiqn.arcadia.items.meta.ArcadiaItemMeta;
import com.datasiqn.arcadia.items.meta.MetaBuilder;
import com.datasiqn.arcadia.items.stats.AttributeInstance;
import com.datasiqn.arcadia.items.stats.AttributeRange;
import com.datasiqn.arcadia.items.stats.ItemAttribute;
import com.datasiqn.arcadia.items.types.ArcadiaMaterial;
import com.datasiqn.arcadia.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class ArcadiaItem {
    private final ItemData itemData;
    private ArcadiaItemMeta itemMeta;

    private @Nullable ArcadiaMaterial material;
    private int amount = 1;

    public ArcadiaItem(@NotNull ArcadiaItem original) {
        this.itemData = original.itemData;
        this.material = original.material;
        this.amount = original.amount;

        this.itemMeta = original.material == null ? new MetaBuilder().build(UUID.randomUUID()) : original.material.createItemMeta(UUID.randomUUID());
    }
    public ArcadiaItem(@NotNull Material material) {
        this(material, 1);
    }
    public ArcadiaItem(@NotNull Material material, int amount) {
        this(ItemUtil.fromDefaultItem(material));
        this.amount = amount;
    }
    public ArcadiaItem(@NotNull ArcadiaMaterial material) {
        this(material, 1);
    }
    public ArcadiaItem(@NotNull ArcadiaMaterial material, int amount) {
        this(material.getItemData());
        this.material = material;
        this.itemMeta = material.createItemMeta(UUID.randomUUID());
        if (!itemData.isStackable()) this.amount = amount;
    }
    public ArcadiaItem(@NotNull ItemStack itemStack) {
        this.amount = itemStack.getAmount();
        ArcadiaMaterial arcadiaMaterial = ItemUtil.getFrom(itemStack);
        if (arcadiaMaterial == null) {
            ItemData data;
            try {
                arcadiaMaterial = ArcadiaMaterial.valueOf(itemStack.getType().name());
                data = arcadiaMaterial.getItemData();
            } catch (IllegalArgumentException ignored) {
                data = ItemUtil.fromDefaultItem(itemStack.getType());
            }
            this.itemData = data;
            this.material = arcadiaMaterial;
            this.itemMeta = arcadiaMaterial == null ? new MetaBuilder().build(UUID.randomUUID()) : arcadiaMaterial.createItemMeta(UUID.randomUUID());
            return;
        }
        this.material = arcadiaMaterial;
        this.itemData = arcadiaMaterial.getItemData();
        if (!itemData.isStackable()) this.amount = 1;
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;
        UUID uuid = UUID.randomUUID();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String strUuid = pdc.get(ArcadiaKeys.ITEM_UUID, PersistentDataType.STRING);
        if (strUuid != null) uuid = UUID.fromString(strUuid);
        this.itemMeta = arcadiaMaterial.createItemMeta(uuid);

        itemMeta.setItemQualityBonus(pdc.getOrDefault(ArcadiaKeys.ITEM_QUALITY_BONUS, PersistentDataType.DOUBLE, 0d));

        EnchantsDataType.EnchantData[] enchantArray = pdc.get(ArcadiaKeys.ITEM_ENCHANTS, new EnchantsDataType());
        if (enchantArray == null) return;
        for (EnchantsDataType.EnchantData data : enchantArray) {
            itemMeta.addEnchant(data.enchantType(), data.level());
        }
    }
    public ArcadiaItem(@NotNull ItemData itemData) {
        this.itemData = itemData;
        this.itemMeta = new MetaBuilder().build(UUID.randomUUID());
    }

    public ItemStack build() {
        return build(amount);
    }
    public ItemStack build(int amount) {
        ItemStack itemStack = itemData.buildWithModifiers(amount, itemMeta.getUuid());
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return itemStack;
        List<String> lore = meta.getLore();
        assert lore != null;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        if (itemMeta.hasEnchants()) {
            meta.addEnchant(Enchantment.DURABILITY, 0, true);

            List<String> enchantLore = new ArrayList<>();
            List<EnchantsDataType.EnchantData> enchantData = new ArrayList<>();

            itemMeta.getEnchants().forEach((type, level) -> {
                NumberFormat numberFormat = NumberFormat.getInstance(Locale.ENGLISH);
                enchantLore.add(ChatColor.BLUE + type.getEnchantment().getName() + " " + numberFormat.format(level));
                enchantData.add(new EnchantsDataType.EnchantData(type, level));
            });
            enchantLore.sort(Comparator.naturalOrder());
            lore.add(0, " ");
            lore.add(0, String.join(", ", enchantLore));

            pdc.set(ArcadiaKeys.ITEM_ENCHANTS, new EnchantsDataType(), enchantData.toArray(enchantData.toArray(new EnchantsDataType.EnchantData[0])));
        }

        if (itemMeta.getItemStats().hasAttributes()) {
            pdc.set(ArcadiaKeys.ITEM_QUALITY_BONUS, PersistentDataType.DOUBLE, itemMeta.getItemQualityBonus());
        }

        if (itemMeta.getItemStats().hasAttributes()) {
            lore.addAll(0, itemMeta.getItemStats().asLore());
            if (itemMeta.getItemStats().hasRandomizedAttributes()) {
                DecimalFormat format = new DecimalFormat("#");
                double itemQuality = itemMeta.getItemQuality() + itemMeta.getItemQualityBonus();
                lore.add(0, ChatColor.DARK_GRAY + "Item Quality: " + (itemQuality >= 1 ? ChatColor.GOLD : ChatColor.DARK_PURPLE) + format.format(itemQuality * 100) + "%");
            } else {
                lore.add(0, ChatColor.DARK_GRAY + "Item Quality: " + ChatColor.GRAY + "100% (never has randomized stats)");
            }
        }

        meta.setLore(lore);
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public ItemStack asCraftingResult() {
        ItemStack craftingResult = itemData.asCraftingResult(amount, itemMeta.getUuid());
        ItemMeta meta = craftingResult.getItemMeta();
        if (meta == null) return craftingResult;
        List<String> lore = meta.getLore();
        assert lore != null;

        if (itemMeta.hasEnchants()) {
            meta.addEnchant(Enchantment.DURABILITY, 0, true);

            List<String> enchantLore = new ArrayList<>();
            List<EnchantsDataType.EnchantData> enchantData = new ArrayList<>();

            itemMeta.getEnchants().forEach((type, level) -> {
                NumberFormat numberFormat = NumberFormat.getInstance(Locale.ENGLISH);
                enchantLore.add(ChatColor.BLUE + type.getEnchantment().getName() + " " + numberFormat.format(level));
                enchantData.add(new EnchantsDataType.EnchantData(type, level));
            });
            enchantLore.sort(Comparator.naturalOrder());
            lore.add(0, " ");
            lore.add(0, String.join(", ", enchantLore));

            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            pdc.set(ArcadiaKeys.ITEM_ENCHANTS, new EnchantsDataType(), enchantData.toArray(enchantData.toArray(new EnchantsDataType.EnchantData[0])));
        }

        if (itemMeta.getItemStats().hasAttributes()) {
            List<String> statsLore = new ArrayList<>();
            for (ItemAttribute attribute : ItemAttribute.values()) {
                AttributeInstance attributeInstance = itemMeta.getItemStats().getAttribute(attribute);
                if (attributeInstance == null) continue;
                AttributeRange attributeRange = attributeInstance.getRange();
                DecimalFormat format = new DecimalFormat("#.##");
                if (Objects.equals(attributeRange.min(), attributeRange.max())) {
                    statsLore.add(ChatColor.GRAY + attribute.toString() + ": +" + attribute.getColor() + format.format(attributeRange.min()) + attribute.getIcon());
                } else {
                    statsLore.add(ChatColor.GRAY + attribute.toString() + ": +" + attribute.getColor() + format.format(attributeRange.min()) + "-" + format.format(attributeRange.max()) + attribute.getIcon());
                }
            }
            statsLore.add(" ");
            lore.addAll(0, statsLore);
            if (itemMeta.getItemStats().hasRandomizedAttributes()) {
                lore.add(0, ChatColor.DARK_GRAY + "Item Quality: " + ChatColor.DARK_PURPLE + "" + ChatColor.MAGIC + "__" + ChatColor.DARK_PURPLE + "%");
            } else {
                lore.add(0, ChatColor.DARK_GRAY + "Item Quality: " + ChatColor.GRAY + "100% (never has randomized stats)");
            }
        }

        meta.setLore(lore);
        craftingResult.setItemMeta(meta);

        return craftingResult;
    }

    public boolean isSimilar(@Nullable ArcadiaItem item) {
        if (item == null) return false;
        return itemData.getID().equals(item.itemData.getID());
    }

    public boolean isDefaultMaterial() {
        return itemData instanceof MaterialItemData;
    }

    public ItemData getItemData() {
        return itemData;
    }

    public @Nullable ArcadiaMaterial getMaterial() {
        return material;
    }

    public @NotNull ArcadiaItemMeta getItemMeta() {
        return itemMeta;
    }

    public static @NotNull ItemStack from(@NotNull Material material, int amount) {
        ArcadiaItem arcadiaItem = new ArcadiaItem(ItemUtil.fromDefaultItem(material));
        arcadiaItem.amount = amount;
        return arcadiaItem.build();
    }
}
