package com.datasiqn.arcadia.managers;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.npc.ArcadiaNpc;
import com.datasiqn.arcadia.npc.CreatedNpc;
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

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class NpcManager {
    private final Arcadia plugin;
    private final Long2ObjectMap<CreatedNpc> createdNPCs = new Long2ObjectOpenHashMap<>();

    private long currentId = 0;

    public NpcManager(Arcadia plugin) {
        this.plugin = plugin;
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
        List<CreatedNpc> visibleNPCs = new ArrayList<>();
        List<CreatedNpc> invisibleNPCs = new ArrayList<>();
        for (CreatedNpc createdNpc : createdNPCs.values()) {
            if (createdNpc.isShown()) visibleNPCs.add(createdNpc);
            else invisibleNPCs.add(createdNpc);
        }

        showPlayer(player, visibleNPCs);
        hidePlayer(player, invisibleNPCs);
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

    public CreatedNpc getNpc(long id) {
        return createdNPCs.get(id);
    }

    public Set<Long> ids() {
        return Collections.unmodifiableSet(createdNPCs.keySet());
    }

    private static void showPlayer(Player player, @NotNull Collection<CreatedNpc> npcs) {
        ServerPlayerConnection connection = ((CraftPlayer) player).getHandle().connection;
        EnumSet<ClientboundPlayerInfoUpdatePacket.Action> actions = EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER);
        connection.send(new ClientboundPlayerInfoUpdatePacket(actions, npcs.stream().map(CreatedNpc::getPlayer).toList()));
        for (CreatedNpc npc : npcs) {
            sendPlayerPackets(connection, npc.getPlayer());
        }
    }

    private static void show(CreatedNpc npc) {
        show(Collections.singleton(npc));
    }
    private static void show(Collection<CreatedNpc> npcs) {
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

    private static void sendPlayerPackets(@NotNull ServerPlayerConnection connection, ServerPlayer serverPlayer) {
        connection.send(new ClientboundAddPlayerPacket(serverPlayer));
        byte skinMask = 0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40;
        List<SynchedEntityData.DataValue<?>> dataValues = List.of(new SynchedEntityData.DataValue<>(17, EntityDataSerializers.BYTE, skinMask));
        connection.send(new ClientboundSetEntityDataPacket(serverPlayer.getId(), dataValues));
        connection.send(new ClientboundRotateHeadPacket(serverPlayer, toAngle(serverPlayer.getXRot())));
        connection.send(new ClientboundMoveEntityPacket.Rot(serverPlayer.getId(), toAngle(serverPlayer.getXRot()), toAngle(serverPlayer.getYRot()), true));
    }

    private static byte toAngle(double delta) {
        return (byte) (delta * 256 / 360);
    }
}
