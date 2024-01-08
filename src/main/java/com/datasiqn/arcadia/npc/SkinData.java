package com.datasiqn.arcadia.npc;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SkinData {
    private SkinType type;

    private UUID uuid;

    private CachedData cachedData;

    private SkinData() {
        this.type = SkinType.NONE;
        this.uuid = null;
        this.cachedData = null;
    }
    private SkinData(UUID uuid) {
        this.type = SkinType.UUID;
        this.uuid = uuid;

        this.cachedData = null;
    }
    private SkinData(String base64, String signature) {
        this.type = SkinType.CACHED;
        this.cachedData = new CachedData(base64, signature);

        this.uuid = null;
    }

    public void cache(String base64, String signature) {
        this.type = SkinType.CACHED;
        this.uuid = null;
        this.cachedData = new CachedData(base64, signature);
    }

    public UUID getUuid() {
        return uuid;
    }

    public CachedData getCachedData() {
        return cachedData;
    }

    public SkinType getType() {
        return type;
    }

    @Contract(value = " -> new", pure = true)
    public static @NotNull SkinData none() {
        return new SkinData();
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull SkinData cached(String base64, String signature) {
        return new SkinData(base64, signature);
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull SkinData uuid(UUID uuid) {
        return new SkinData(uuid);
    }

    public record CachedData(String base64, String signature) { }

    public enum SkinType {
        NONE,
        CACHED,
        UUID,
    }
}
