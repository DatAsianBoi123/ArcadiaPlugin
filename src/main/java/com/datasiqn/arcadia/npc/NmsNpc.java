package com.datasiqn.arcadia.npc;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class NmsNpc extends ServerPlayer {
    private String interactCommand;

    public NmsNpc(MinecraftServer minecraftserver, ServerLevel world, GameProfile gameprofile) {
        super(minecraftserver, world, gameprofile);
    }

    public String getInteractCommand() {
        return interactCommand;
    }

    public void setInteractCommand(String interactCommand) {
        this.interactCommand = interactCommand;
    }

    @Override
    public InteractionResult interact(@NotNull Player player, InteractionHand hand) {
        if (hand == InteractionHand.OFF_HAND || interactCommand == null || interactCommand.isEmpty()) {
            return InteractionResult.PASS;
        }
        Bukkit.dispatchCommand(player.getBukkitEntity(), interactCommand);
        return InteractionResult.PASS;
    }
}
