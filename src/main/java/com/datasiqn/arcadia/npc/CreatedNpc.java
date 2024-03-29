package com.datasiqn.arcadia.npc;

import com.datasiqn.arcadia.Arcadia;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.entity.Display.TextDisplay;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public final class CreatedNpc {
    private final long id;
    private final ArcadiaNpc npc;
    private final NmsNpc serverPlayer;
    private final TextDisplay nameDisplay;

    private boolean shown = false;

    public CreatedNpc(long id, @NotNull ArcadiaNpc npc, @NotNull NmsNpc serverPlayer) {
        this.id = id;
        this.npc = npc;
        this.serverPlayer = serverPlayer;
        this.nameDisplay = new TextDisplay(EntityType.TEXT_DISPLAY, serverPlayer.level);
        nameDisplay.setPos(serverPlayer.position().add(0, 2, 0));
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

    public NmsNpc getPlayer() {
        return serverPlayer;
    }

    public TextDisplay getNameDisplay() {
        return nameDisplay;
    }

    public boolean isShown() {
        return shown;
    }

    public @NotNull JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("shown", shown);
        jsonObject.addProperty("command", serverPlayer.getInteractCommand());
        jsonObject.add("npc", npc.toJson());
        return jsonObject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CreatedNpc that = (CreatedNpc) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    public static @NotNull CompletableFuture<CreatedNpc> fromJson(Arcadia plugin, @NotNull JsonObject jsonObject) {
        ArcadiaNpc npc = ArcadiaNpc.fromJson(jsonObject.get("npc").getAsJsonObject());
        return npc.createFakePlayer(plugin)
                .thenApply(serverPlayer -> {
                    CreatedNpc createdNpc = new CreatedNpc(jsonObject.get("id").getAsLong(), npc, serverPlayer);
                    createdNpc.shown = jsonObject.get("shown").getAsBoolean();
                    JsonElement commandElement = jsonObject.get("command");
                    if (commandElement != null) serverPlayer.setInteractCommand(commandElement.getAsString());
                    return createdNpc;
                });
    }
}
