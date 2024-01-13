package com.datasiqn.arcadia.managers;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.npc.ArcadiaNpc;
import com.datasiqn.arcadia.npc.CreatedNpc;
import com.google.gson.*;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class NpcManager {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final Arcadia plugin;
    private final File saveFile;
    private final Long2ObjectMap<CreatedNpc> createdNPCs = new Long2ObjectOpenHashMap<>();
    private final Map<UUID, CreatedNpc> selectedNpcs = new HashMap<>();

    private long currentId = 0;

    public NpcManager(Arcadia plugin, File saveFile) {
        this.plugin = plugin;
        this.saveFile = saveFile;
    }

    public CompletableFuture<CreatedNpc> create(@NotNull ArcadiaNpc npc) {
        return npc.createFakePlayer(plugin)
                .thenApply(serverPlayer -> {
                    CreatedNpc createdNpc = new CreatedNpc(currentId, npc, serverPlayer);
                    createdNPCs.put(currentId, createdNpc);
                    currentId++;
                    return createdNpc;
                });
    }

    public void updateForPlayer(Player player) {
        for (CreatedNpc createdNpc : createdNPCs.values()) {
            if (createdNpc.isShown()) showPlayer(player, Collections.singleton(createdNpc));
            else hidePlayer(player, Collections.singleton(createdNpc));
        }
    }

    public void updateVisibility(long id) {
        CreatedNpc createdNpc = createdNPCs.get(id);
        if (createdNpc == null) return;
        if (createdNpc.isShown()) {
            show(createdNpc);
        } else {
            hide(createdNpc);
        }
    }

    public void updateVisibilities() {
        List<CreatedNpc> visibleNPCs = new ArrayList<>();
        List<CreatedNpc> invisibleNPCs = new ArrayList<>();
        for (CreatedNpc createdNpc : createdNPCs.values()) {
            if (createdNpc.isShown()) visibleNPCs.add(createdNpc);
            else invisibleNPCs.add(createdNpc);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            showPlayer(player, visibleNPCs);
            hidePlayer(player, invisibleNPCs);
        }
    }

    public void destroy(long id) {
        CreatedNpc createdNpc = createdNPCs.remove(id);
        if (createdNpc == null || !createdNpc.isShown()) return;
        hide(createdNpc);
    }

    public void destroyAll() {
        for (long id : createdNPCs.keySet()) {
            destroy(id);
        }
    }

    public void selectNpc(Player player, long id) {
        CreatedNpc npc = createdNPCs.get(id);
        if (npc == null) return;
        CreatedNpc previouslySelected = selectedNpcs.put(player.getUniqueId(), npc);
        if (!npc.isShown()) return;
        displayGlow(player, npc);
        if (previouslySelected != null) removeGlow(player, previouslySelected);
    }

    public void deselectNpc(@NotNull Player player) {
        CreatedNpc npc = selectedNpcs.remove(player.getUniqueId());
        if (npc == null) return;
        removeGlow(player, npc);
    }

    public CreatedNpc getNpc(long id) {
        return createdNPCs.get(id);
    }

    public CreatedNpc getSelectedNpc(@NotNull Player player) {
        return selectedNpcs.get(player.getUniqueId());
    }

    public Set<Long> ids() {
        return Collections.unmodifiableSet(createdNPCs.keySet());
    }

    public CompletableFuture<Void> save() {
        JsonObject rootObject = new JsonObject();
        rootObject.addProperty("currentId", currentId);

        JsonArray npcArray = new JsonArray();
        for (CreatedNpc npc : createdNPCs.values()) {
            npcArray.add(npc.toJson());
        }
        rootObject.add("npcs", npcArray);

        return CompletableFuture.runAsync(() -> {
            try {
                if (!saveFile.exists() && saveFile.createNewFile()) plugin.getLogger().info("Created NPC data file");
                FileWriter writer = new FileWriter(saveFile);
                gson.toJson(rootObject, writer);
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<Void> load() {
        if (!saveFile.exists()) return CompletableFuture.completedFuture(null);

        return CompletableFuture.runAsync(() -> {
            try {
                FileReader reader = new FileReader(saveFile);
                JsonObject npcData = gson.fromJson(reader, JsonObject.class);
                reader.close();
                currentId = npcData.get("currentId").getAsLong();
                for (JsonElement npcElement : npcData.get("npcs").getAsJsonArray()) {
                    CreatedNpc createdNpc = CreatedNpc.fromJson(plugin, npcElement.getAsJsonObject()).join();
                    createdNPCs.put(createdNpc.getId(), createdNpc);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void showPlayer(Player player, @NotNull Collection<CreatedNpc> npcs) {
        ServerPlayerConnection connection = ((CraftPlayer) player).getHandle().connection;
        EnumSet<ClientboundPlayerInfoUpdatePacket.Action> actions = EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER);
        connection.send(new ClientboundPlayerInfoUpdatePacket(actions, npcs.stream().map(CreatedNpc::getPlayer).toList()));
        for (CreatedNpc npc : npcs) {
            boolean glow = false;
            CreatedNpc selectedNpc = selectedNpcs.get(player.getUniqueId());
            if (selectedNpc != null) glow = selectedNpc.equals(npc);
            sendPlayerPackets(connection, npc.getPlayer(), glow);
        }
    }

    private void show(CreatedNpc npc) {
        show(Collections.singleton(npc));
    }
    private void show(Collection<CreatedNpc> npcs) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            showPlayer(player, npcs);
        }
    }

    private static void hidePlayer(Player player, @NotNull Collection<CreatedNpc> npcs) {
        ((CraftPlayer) player).getHandle().connection.send(new ClientboundRemoveEntitiesPacket(npcs.stream().mapToInt(npc -> npc.getPlayer().getId()).toArray()));
    }

    private static void hide(CreatedNpc npc) {
        hide(Collections.singleton(npc));
    }
    private static void hide(Collection<CreatedNpc> npcs) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            hidePlayer(player, npcs);
        }
    }

    private static void displayGlow(Player player, @NotNull CreatedNpc npc) {
        sendDataPacket(((CraftPlayer) player).getHandle().connection, npc.getPlayer(), true);
    }

    private static void removeGlow(Player player, @NotNull CreatedNpc npc) {
        sendDataPacket(((CraftPlayer) player).getHandle().connection, npc.getPlayer(), false);
    }

    private static void sendPlayerPackets(@NotNull ServerPlayerConnection connection, ServerPlayer serverPlayer, boolean glow) {
        connection.send(new ClientboundAddPlayerPacket(serverPlayer));
        sendDataPacket(connection, serverPlayer, glow);
        connection.send(new ClientboundRotateHeadPacket(serverPlayer, toAngle(serverPlayer.getXRot())));
        connection.send(new ClientboundMoveEntityPacket.Rot(serverPlayer.getId(), toAngle(serverPlayer.getXRot()), toAngle(serverPlayer.getYRot()), true));
    }

    private static void sendDataPacket(@NotNull ServerPlayerConnection connection, @NotNull ServerPlayer serverPlayer, boolean glow) {
        byte skinMask = 0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40;
        List<SynchedEntityData.DataValue<?>> dataValues = new ArrayList<>();
        dataValues.add(new SynchedEntityData.DataValue<>(17, EntityDataSerializers.BYTE, skinMask));
        dataValues.add(new SynchedEntityData.DataValue<>(0, EntityDataSerializers.BYTE, glow ? (byte) 0x40 : 0));
        connection.send(new ClientboundSetEntityDataPacket(serverPlayer.getId(), dataValues));
    }

    private static byte toAngle(double delta) {
        return (byte) (delta * 256 / 360);
    }
}
