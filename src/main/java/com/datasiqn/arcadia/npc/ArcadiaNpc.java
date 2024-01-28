package com.datasiqn.arcadia.npc;

import com.datasiqn.arcadia.Arcadia;
import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class ArcadiaNpc {
    private final World world;
    private final Location location;
    private final String name;
    private final UUID uuid;
    private final SkinData skinData;

    public ArcadiaNpc(World world, Location location, String name, SkinData skinData) {
        this(world, location, name, skinData, UUID.randomUUID());
    }
    public ArcadiaNpc(World world, Location location, String name, SkinData skinData, UUID uuid) {
        this.world = world;
        this.location = location;
        this.name = name;
        this.skinData = skinData;
        this.uuid = uuid;
    }

    public @NotNull CompletableFuture<NmsNpc> createFakePlayer(Arcadia plugin) {
        return CompletableFuture
                .supplyAsync(() -> {
                    GameProfile gameProfile = new GameProfile(uuid, name);
                    if (skinData.getType() == SkinData.SkinType.UUID) {
                        try {
                            HttpsURLConnection connection = (HttpsURLConnection) new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + skinData.getUuid() + "?unsigned=false").openConnection();
                            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                                JsonObject skinJson = JsonParser.parseReader(new InputStreamReader(connection.getInputStream())).getAsJsonObject();
                                String skinBase64 = null;
                                String skinSignature = null;
                                for (JsonElement property : skinJson.get("properties").getAsJsonArray()) {
                                    JsonObject propertyObject = property.getAsJsonObject();
                                    if (!propertyObject.get("name").getAsString().equals("textures")) continue;
                                    skinBase64 = propertyObject.get("value").getAsString();
                                    skinSignature = propertyObject.get("signature").getAsString();
                                    break;
                                }
                                if (skinBase64 == null) throw new IllegalStateException("this wasn't supposed to happen, skin api changed?");
                                skinData.cache(skinBase64, skinSignature);
                            } else {
                                plugin.getLogger().severe("Error connecting to skin servers: " + connection.getResponseMessage());
                            }
                        } catch (MalformedURLException e) {
                            plugin.getLogger().severe("Malformed URL! This shouldn't ever happen");
                            return gameProfile;
                        } catch (IOException e) {
                            plugin.getLogger().severe("Could not connect to mojang API server: " + e.getMessage());
                            return gameProfile;
                        }
                    }

                    if (skinData.getType() == SkinData.SkinType.CACHED) {
                        SkinData.CachedData cachedData = skinData.getCachedData();
                        gameProfile.getProperties().put("textures", new Property("textures", cachedData.base64(), cachedData.signature()));
                    }
                    return gameProfile;
                })
                .exceptionally(throwable -> {
                    plugin.getLogger().severe("uh oh, error occurred when getting skin info: " + throwable.getMessage());
                    return new GameProfile(UUID.randomUUID(), name);
                })
                .thenApply(gameProfile -> {
                    DedicatedServer server = ((CraftServer) Bukkit.getServer()).getServer();
                    ServerLevel serverWorld = ((CraftWorld) world).getHandle();
                    NmsNpc serverPlayer = new NmsNpc(server, serverWorld, gameProfile);
                    serverPlayer.setPos(location.getX(), location.getY(), location.getZ());
                    serverPlayer.setXRot(location.getYaw());
                    serverPlayer.setYRot(location.getPitch());
                    return serverPlayer;
        });
    }

    public World getWorld() {
        return world;
    }

    public Location getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public @NotNull JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("uuid", uuid.toString());
        jsonObject.addProperty("world", world.getUID().toString());
        JsonObject locationObject = new JsonObject();
        locationObject.addProperty("x", location.getX());
        locationObject.addProperty("y", location.getY());
        locationObject.addProperty("z", location.getZ());
        locationObject.addProperty("yaw", location.getYaw());
        locationObject.addProperty("pitch", location.getPitch());
        jsonObject.add("location", locationObject);

        JsonElement skinElement = JsonNull.INSTANCE;
        SkinData.SkinType skinType = skinData.getType();
        if (skinType == SkinData.SkinType.CACHED) {
            JsonObject cached = new JsonObject();
            SkinData.CachedData cachedData = skinData.getCachedData();
            cached.addProperty("value", cachedData.base64());
            cached.addProperty("signature", cachedData.signature());
            skinElement = cached;
        } else if (skinType == SkinData.SkinType.UUID) {
            skinElement = new JsonPrimitive(skinData.getUuid().toString());
        }
        jsonObject.add("skin", skinElement);

        return jsonObject;
    }

    public static @NotNull ArcadiaNpc fromJson(@NotNull JsonObject jsonObject) {
        World world = Bukkit.getWorld(UUID.fromString(jsonObject.get("world").getAsString()));
        JsonObject locationObject = jsonObject.get("location").getAsJsonObject();
        double x = locationObject.get("x").getAsDouble();
        double y = locationObject.get("y").getAsDouble();
        double z = locationObject.get("z").getAsDouble();
        float yaw = locationObject.get("yaw").getAsFloat();
        float pitch = locationObject.get("pitch").getAsFloat();
        Location location = new Location(world, x, y, z, yaw, pitch);
        String name = jsonObject.get("name").getAsString();
        UUID uuid = UUID.fromString(jsonObject.get("uuid").getAsString());

        JsonElement skinElement = jsonObject.get("skin");
        SkinData skinData;
        if (skinElement == null) {
            skinData = SkinData.none();
        } else if (skinElement.isJsonObject()) {
            JsonObject cachedSkin = skinElement.getAsJsonObject();
            skinData = SkinData.cached(cachedSkin.get("value").getAsString(), cachedSkin.get("signature").getAsString());
        } else {
            skinData = SkinData.uuid(UUID.fromString(skinElement.getAsString()));
        }
        return new ArcadiaNpc(world, location, name, skinData, uuid);
    }
}
