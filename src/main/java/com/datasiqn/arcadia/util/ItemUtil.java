package com.datasiqn.arcadia.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
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
