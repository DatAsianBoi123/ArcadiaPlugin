package com.datasiqn.arcadia.items.modifiers;

import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class SkullItemModifier implements ItemModifier {
    private final String skinId;

    public SkullItemModifier(String skinId) {
        this.skinId = skinId;
    }

    @Override
    public void modify(@NotNull UUID uuid, @Nullable ItemMeta metaCopy) {
        if (!(metaCopy instanceof SkullMeta skullMeta)) return;

        PlayerProfile playerProfile = Bukkit.getServer().createPlayerProfile(uuid);
        try {
            playerProfile.getTextures().setSkin(new URL("https://textures.minecraft.net/texture/" + skinId));
        } catch (MalformedURLException ignored) {}
        skullMeta.setOwnerProfile(playerProfile);
    }
}
