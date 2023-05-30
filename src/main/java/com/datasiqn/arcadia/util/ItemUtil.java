package com.datasiqn.arcadia.util;

import com.datasiqn.arcadia.ArcadiaTag;
import com.datasiqn.arcadia.items.ArcadiaItem;
import com.datasiqn.arcadia.items.materials.ArcadiaMaterial;
import com.datasiqn.arcadia.items.materials.data.DefaultMaterialData;
import com.datasiqn.arcadia.items.materials.data.MaterialData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.profile.PlayerProfile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class ItemUtil {
    private ItemUtil() {}

    public static void setHeadSkin(@NotNull SkullMeta meta, String skinId, UUID uuid) {
        PlayerProfile playerProfile = Bukkit.createPlayerProfile(uuid);
        try {
            playerProfile.getTextures().setSkin(new URL("https://textures.minecraft.net/texture/" + skinId));
        } catch (MalformedURLException ignored) {}
        meta.setOwnerProfile(playerProfile);
    }

    @Contract("_ -> new")
    public static @NotNull MaterialData<?> fromDefaultItem(@NotNull Material type) {
        return new DefaultMaterialData(type);
    }

    public static @Nullable ArcadiaMaterial getFrom(@NotNull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return null;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (!PdcUtil.has(pdc, ArcadiaTag.ITEM_ID)) return null;
        if (PdcUtil.getOrDefault(pdc, ArcadiaTag.ITEM_MATERIAL, false)) return null;

        return PdcUtil.get(pdc, ArcadiaTag.ITEM_ID).getMaterial();
    }

    public static @NotNull ItemStack createEmpty(Material material) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return itemStack;
        meta.setDisplayName(" ");
        meta.addItemFlags(ItemFlag.values());
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static @NotNull Collection<ArcadiaItem> splitItem(@NotNull ArcadiaItem original, int times) {
        Set<ArcadiaItem> itemSet = new HashSet<>(times);
        int amount = Math.floorDiv(original.getAmount(), times);
        int extraAmounts = original.getAmount() % times;
        for (int i = 0; i < times; i++) {
            ArcadiaItem item = new ArcadiaItem(original);
            if (extraAmounts > 0) {
                item.setAmount(amount + 1);
                extraAmounts -= 1;
            } else {
                item.setAmount(amount);
            }
            if (item.getAmount() > 0) itemSet.add(item);
        }
        return itemSet;
    }
}
