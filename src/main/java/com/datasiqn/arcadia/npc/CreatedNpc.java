package com.datasiqn.arcadia.npc;

import net.minecraft.server.level.ServerPlayer;

public final class CreatedNpc {
    private final long id;
    private final ArcadiaNpc npc;
    private final ServerPlayer serverPlayer;

    private boolean shown = false;

    public CreatedNpc(long id, ArcadiaNpc npc, ServerPlayer serverPlayer) {
        this.id = id;
        this.npc = npc;
        this.serverPlayer = serverPlayer;
    }

    public void show() {
        shown = true;
    }

    public void hide() {
        shown = false;
    }

    public long getId() {
        return id;
    }

    public ArcadiaNpc getNpc() {
        return npc;
    }

    public ServerPlayer getPlayer() {
        return serverPlayer;
    }

    public boolean isShown() {
        return shown;
    }
}
