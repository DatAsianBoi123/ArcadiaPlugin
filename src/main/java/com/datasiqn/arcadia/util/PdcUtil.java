package com.datasiqn.arcadia.util;

import com.datasiqn.arcadia.ArcadiaTag;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public final class PdcUtil {
    private static Plugin plugin;

    public static <T> void set(@NotNull PersistentDataContainer container, @NotNull ArcadiaTag<T> tag, @NotNull T value) {
        container.set(new NamespacedKey(plugin, tag.getKey()), tag.getDataType(), value);
    }

    public static <T> T get(@NotNull PersistentDataContainer container, @NotNull ArcadiaTag<T> tag) {
        return container.get(new NamespacedKey(plugin, tag.getKey()), tag.getDataType());
    }

    public static <T> @NotNull T getOrDefault(@NotNull PersistentDataContainer container, @NotNull ArcadiaTag<T> tag, T def) {
        return container.getOrDefault(new NamespacedKey(plugin, tag.getKey()), tag.getDataType(), def);
    }

    public static <T> boolean has(@NotNull PersistentDataContainer container, @NotNull ArcadiaTag<T> tag) {
        return container.has(new NamespacedKey(plugin, tag.getKey()), tag.getDataType());
    }

    public static <T> void remove(@NotNull PersistentDataContainer container, @NotNull ArcadiaTag<T> tag) {
        container.remove(new NamespacedKey(plugin, tag.getKey()));
    }

    public static void setPlugin(Plugin plugin) {
        PdcUtil.plugin = plugin;
    }
}
