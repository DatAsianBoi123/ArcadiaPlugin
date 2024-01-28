package com.datasiqn.arcadia.player;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.managers.NpcManager;
import com.datasiqn.arcadia.npc.CreatedNpc;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class ArcadiaPacketListener extends ServerGamePacketListenerImpl {
    private final Arcadia plugin;

    public ArcadiaPacketListener(ServerGamePacketListenerImpl packetListener, Arcadia plugin) {
        super(getField(packetListener, "i" /* server */), getField(packetListener, "h" /* connection */), packetListener.player);

        this.plugin = plugin;
    }

    @Override
    public void handleInteract(ServerboundInteractPacket packet) {
        super.handleInteract(packet);

        int entityId = getField(packet, "a" /* entityId */);
        packet.dispatch(new ServerboundInteractPacket.Handler() {
            @Override
            public void onInteraction(InteractionHand interactionHand) {
                handle(interactionHand);
            }

            @Override
            public void onInteraction(InteractionHand interactionHand, Vec3 vec3) { }

            @Override
            public void onAttack() {
                handle(InteractionHand.MAIN_HAND);
            }

            private void handle(InteractionHand hand) {
                NpcManager npcManager = plugin.getNpcManager();
                for (long npcId : npcManager.ids()) {
                    CreatedNpc npc = npcManager.getNpc(npcId);
                    if (npc.getPlayer().getId() != entityId) continue;
                    npc.getPlayer().interact(player, hand);
                    break;
                }
            }
        });
    }

    private static  <T> T getField(@NotNull Object instance, String fieldName) {
        try {
            Field field = instance.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            //noinspection unchecked
            return (T) field.get(instance);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
