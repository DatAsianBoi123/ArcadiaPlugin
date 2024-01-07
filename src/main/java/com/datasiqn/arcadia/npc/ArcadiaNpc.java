package com.datasiqn.arcadia.npc;

import com.datasiqn.arcadia.Arcadia;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class ArcadiaNpc {
    private final World world;
    private final Location location;
    private final String name;
    private final UUID skinUuid;

    private String skinBase64;

    public ArcadiaNpc(World world, Location location, String name) {
        this(world, location, name, (UUID) null);
    }
    public ArcadiaNpc(World world, Location location, String name, UUID skinUuid) {
        this.world = world;
        this.location = location;
        this.name = name;
        this.skinUuid = skinUuid;
    }
    public ArcadiaNpc(World world, Location location, String name, String skinBase64) {
        this(world, location, name);
        this.skinBase64 = skinBase64;
    }

    public @NotNull CompletableFuture<ServerPlayer> createFakePlayer(Arcadia plugin) {
        return CompletableFuture
                .supplyAsync(() -> {
                    GameProfile gameProfile = new GameProfile(UUID.randomUUID(), name);
                    if (skinUuid == null) return gameProfile;
                    String skinSignature = null;
                    if (skinBase64 == null) {
                        try {
                            HttpsURLConnection connection = (HttpsURLConnection) new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + skinUuid + "?unsigned=false").openConnection();
                            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                                JsonObject skinJson = JsonParser.parseReader(new InputStreamReader(connection.getInputStream())).getAsJsonObject();
                                for (JsonElement property : skinJson.get("properties").getAsJsonArray()) {
                                    JsonObject propertyObject = property.getAsJsonObject();
                                    if (!propertyObject.get("name").getAsString().equals("textures")) continue;
                                    skinBase64 = propertyObject.get("value").getAsString();
                                    skinSignature = propertyObject.get("signature").getAsString();
                                    break;
                                }
                            } else {
                                plugin.getLogger().severe("Error connecting to skin servers: " + connection.getResponseMessage());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            return gameProfile;
                        }
                    }
                    gameProfile.getProperties().put("textures", new Property("textures", skinBase64, skinSignature));
                    return gameProfile;
                })
                .exceptionally(throwable -> {
                    plugin.getLogger().severe("uh oh, error occurred when getting skin info: " + throwable.getMessage());
                    return new GameProfile(UUID.randomUUID(), name);
                })
                .thenApply(gameProfile -> {
                    DedicatedServer server = ((CraftServer) Bukkit.getServer()).getServer();
                    ServerLevel serverWorld = ((CraftWorld) world).getHandle();
                    ServerPlayer serverPlayer = new ServerPlayer(server, serverWorld, gameProfile);
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
}
