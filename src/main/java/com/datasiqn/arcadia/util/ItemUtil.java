package com.datasiqn.arcadia.util;

import com.datasiqn.arcadia.ArcadiaKeys;
import com.datasiqn.arcadia.items.materials.data.MaterialData;
import com.datasiqn.arcadia.items.materials.data.DefaultMaterialData;
import com.datasiqn.arcadia.items.materials.ArcadiaMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public final class ItemUtil {
    private ItemUtil() {}

    public static void setHeadSkin(SkullMeta meta, String skinId, UUID uuid) {
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
        if (!pdc.has(ArcadiaKeys.ITEM_ID, PersistentDataType.STRING)) return null;
        if (pdc.has(ArcadiaKeys.ITEM_MATERIAL, PersistentDataType.BYTE)) {
            Byte bool = pdc.get(ArcadiaKeys.ITEM_MATERIAL, PersistentDataType.BYTE);
            assert bool != null;
            if (bool == (byte) 1) return null;
        }

        String id = pdc.get(ArcadiaKeys.ITEM_ID, PersistentDataType.STRING);
        return ArcadiaMaterial.valueOf(id);
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
}
