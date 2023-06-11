package com.datasiqn.arcadia.item;

import com.datasiqn.arcadia.ArcadiaTag;
import com.datasiqn.arcadia.datatypes.EnchantsDataType;
import com.datasiqn.arcadia.item.material.ArcadiaMaterial;
import com.datasiqn.arcadia.item.material.data.DefaultMaterialData;
import com.datasiqn.arcadia.item.material.data.MaterialData;
import com.datasiqn.arcadia.item.meta.ArcadiaItemMeta;
import com.datasiqn.arcadia.item.stat.AttributeInstance;
import com.datasiqn.arcadia.item.stat.AttributeRange;
import com.datasiqn.arcadia.item.stat.ItemAttribute;
import com.datasiqn.arcadia.util.ItemUtil;
import com.datasiqn.arcadia.util.PdcUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class ArcadiaItem implements ConfigurationSerializable {
    private final @NotNull MaterialData<?> itemData;
    private final @NotNull ArcadiaItemMeta itemMeta;

    private @Nullable ArcadiaMaterial material;
    private int amount = 1;

    public ArcadiaItem(@NotNull ArcadiaItem original) {
        this.itemData = original.itemData;
        this.material = original.material;
        this.amount = original.amount;

        if (original.material == null) {
            this.itemMeta = new ArcadiaItemMeta(UUID.randomUUID());
        } else {
            ArcadiaItemMeta meta = original.material.createItemMeta(UUID.randomUUID());
            ArcadiaItemMeta originalMeta = original.itemMeta;
            meta.setItemQualityBonus(originalMeta.getItemQualityBonus());
            originalMeta.getEnchants().forEach(type -> meta.addEnchant(type, originalMeta.getEnchantLevel(type)));
            this.itemMeta = meta;
        }
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
        this(material.getData(), material.createItemMeta(UUID.randomUUID()));
        this.material = material;
        if (itemData.isStackable()) this.amount = amount;
    }

    public ArcadiaItem(@NotNull ItemStack itemStack) {
        this.amount = itemStack.getAmount();
        ArcadiaMaterial arcadiaMaterial = ItemUtil.getFrom(itemStack);
        if (arcadiaMaterial == null) {
            MaterialData<?> data;
            try {
                arcadiaMaterial = ArcadiaMaterial.valueOf(itemStack.getType().name());
                data = arcadiaMaterial.getData();
            } catch (IllegalArgumentException ignored) {
                data = ItemUtil.fromDefaultItem(itemStack.getType());
            }
            this.itemData = data;
            this.material = arcadiaMaterial;
            this.itemMeta = arcadiaMaterial == null ? new ArcadiaItemMeta(UUID.randomUUID()) : arcadiaMaterial.createItemMeta(UUID.randomUUID());
            return;
        }
        this.material = arcadiaMaterial;
        this.itemData = arcadiaMaterial.getData();
        if (!itemData.isStackable()) this.amount = 1;
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            this.itemMeta = arcadiaMaterial.createItemMeta(UUID.randomUUID());
            return;
        }
        ArcadiaItemMeta meta1 = new ArcadiaItemMeta(meta.getPersistentDataContainer());
        this.itemMeta = arcadiaMaterial.createItemMeta(meta1.getUuid());

        itemMeta.setItemQualityBonus(meta1.getItemQualityBonus());
        meta1.getEnchants().forEach(type -> itemMeta.addEnchant(type, meta1.getEnchantLevel(type)));
    }

    public ArcadiaItem(@NotNull MaterialData<?> itemData) {
        this(itemData, new ArcadiaItemMeta(UUID.randomUUID()));
    }
    public ArcadiaItem(@NotNull MaterialData<?> itemData, @NotNull ArcadiaItemMeta meta) {
        this.itemData = itemData;
        this.itemMeta = meta;
    }

    public ItemStack build() {
        ItemStack itemStack = itemData.toItemStack(amount, itemMeta.getUuid());
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return itemStack;
        PdcUtil.set(meta.getPersistentDataContainer(), ArcadiaTag.ITEM_ID, getId());
        List<String> lore = meta.getLore();
        assert lore != null;

        if (itemMeta.hasEnchants()) {
            meta.addEnchant(Enchantment.DURABILITY, 0, true);

            List<String> enchantLore = new ArrayList<>();

            itemMeta.getEnchants().forEach(type -> {
                NumberFormat numberFormat = NumberFormat.getInstance(Locale.ENGLISH);
                enchantLore.add(ChatColor.BLUE + type.getEnchantment().getName() + " " + numberFormat.format(itemMeta.getEnchantLevel(type)));
            });
            enchantLore.sort(Comparator.naturalOrder());
            lore.add(0, " ");
            lore.add(0, String.join(", ", enchantLore));
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

        itemMeta.addToPdc(meta.getPersistentDataContainer());

        meta.setLore(lore);
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public ItemStack asCraftingResult() {
        ItemStack craftingResult = itemData.asCraftingResult(amount, itemMeta.getUuid());
        ItemMeta meta = craftingResult.getItemMeta();
        if (meta == null) return craftingResult;
        PdcUtil.set(meta.getPersistentDataContainer(), ArcadiaTag.ITEM_ID, getId());
        List<String> lore = meta.getLore();
        assert lore != null;

        if (itemMeta.hasEnchants()) {
            meta.addEnchant(Enchantment.DURABILITY, 0, true);

            List<String> enchantLore = new ArrayList<>();
            List<EnchantsDataType.EnchantData> enchantData = new ArrayList<>();

            itemMeta.getEnchants().forEach(type -> {
                NumberFormat numberFormat = NumberFormat.getInstance(Locale.ENGLISH);
                int level = itemMeta.getEnchantLevel(type);
                enchantLore.add(ChatColor.BLUE + type.getEnchantment().getName() + " " + numberFormat.format(level));
                enchantData.add(new EnchantsDataType.EnchantData(type, level));
            });
            enchantLore.sort(Comparator.naturalOrder());
            lore.add(0, " ");
            lore.add(0, String.join(", ", enchantLore));

            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            PdcUtil.set(pdc, ArcadiaTag.ITEM_ENCHANTS, enchantData.toArray(EnchantsDataType.EnchantData[]::new));
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
        return getId().equals(item.getId());
    }

    public boolean isDefaultMaterial() {
        return itemData instanceof DefaultMaterialData;
    }

    public @NotNull ItemId getId() {
        return material == null ? ItemId.fromVanillaMaterial(itemData.getMaterial()) : ItemId.fromArcadiaMaterial(material);
    }

    public @NotNull MaterialData<?> getItemData() {
        return itemData;
    }

    public @Nullable ArcadiaMaterial getMaterial() {
        return material;
    }

    public @NotNull ArcadiaItemMeta getItemMeta() {
        return itemMeta;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("id", getId().getStringId());
        objectMap.put("amount", amount);
        return objectMap;
    }

    public static @NotNull ArcadiaItem deserialize(@NotNull Map<String, Object> data) {
        String id = data.get("id") instanceof String string ? string.toUpperCase() : "STONE";
        ArcadiaItem item;
        try {
            item = new ArcadiaItem(ArcadiaMaterial.valueOf(id));
        } catch (IllegalArgumentException e) {
            Material matchMaterial = Material.matchMaterial(id);
            if (matchMaterial == null) matchMaterial = Material.STONE;
            item = new ArcadiaItem(ItemUtil.fromDefaultItem(matchMaterial));
        }
        item.amount = data.get("amount") instanceof Integer integer ? integer : 1;
        return item;
    }

    public static @NotNull ItemStack from(@NotNull Material material, int amount) {
        ArcadiaItem arcadiaItem = new ArcadiaItem(ItemUtil.fromDefaultItem(material));
        arcadiaItem.amount = amount;
        return arcadiaItem.build();
    }
}
