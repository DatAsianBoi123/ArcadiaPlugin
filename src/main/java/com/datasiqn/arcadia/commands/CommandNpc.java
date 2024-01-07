package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.commands.argument.ArcadiaArgumentType;
import com.datasiqn.arcadia.managers.NpcManager;
import com.datasiqn.arcadia.npc.ArcadiaNpc;
import com.datasiqn.arcadia.npc.CreatedNpc;
import com.datasiqn.commandcore.argument.Arguments;
import com.datasiqn.commandcore.argument.type.ArgumentType;
import com.datasiqn.commandcore.command.builder.ArgumentBuilder;
import com.datasiqn.commandcore.command.builder.CommandBuilder;
import com.datasiqn.commandcore.command.builder.LiteralBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CommandNpc {
    private final Arcadia plugin;

    @Contract(pure = true)
    public CommandNpc(@NotNull Arcadia plugin) {
        this.plugin = plugin;
    }

    public CommandBuilder getCommand() {
        NpcManager npcManager = plugin.getNpcManager();
        return new CommandBuilder("npc")
                .description("NPC related commands")
                .permission("arcadia.npc")
                .then(LiteralBuilder.literal("create")
                        .then(ArgumentBuilder.argument(ArgumentType.QUOTED_WORD, "npc name")
                                .then(ArgumentBuilder.argument(ArgumentType.UUID, "skin uuid")
                                        .requiresPlayer()
                                        .executes((context, source, arguments) -> handleCreate(source.getPlayer(), arguments)))
                                .executes((context, source, arguments) -> handleCreate(source.getPlayer(), arguments))))
                .then(LiteralBuilder.literal("show")
                        .then(LiteralBuilder.literal("all")
                                .executes((context, source, arguments) -> {
                                    for (long id : npcManager.ids()) {
                                        CreatedNpc npc = npcManager.getNpc(id);
                                        npc.show();
                                    }
                                    npcManager.updateVisibilities();
                                }))
                        .then(ArgumentBuilder.argument(ArcadiaArgumentType.NPC, "npc id")
                                .executes((context, source, arguments) -> {
                                    CreatedNpc npc = arguments.get(1, ArcadiaArgumentType.NPC);
                                    npc.show();
                                    npcManager.updateVisibility(npc.getId());
                                })))
                .then(LiteralBuilder.literal("hide")
                        .then(LiteralBuilder.literal("all")
                                .executes((context, source, arguments) -> {
                                    for (long id : npcManager.ids()) {
                                        CreatedNpc npc = npcManager.getNpc(id);
                                        npc.hide();
                                    }
                                    npcManager.updateVisibilities();
                                }))
                        .then(ArgumentBuilder.argument(ArcadiaArgumentType.NPC, "npc id")
                                .executes((context, source, arguments) -> {
                                    CreatedNpc npc = arguments.get(1, ArcadiaArgumentType.NPC);
                                    npc.hide();
                                    npcManager.updateVisibility(npc.getId());
                                })))
                .then(LiteralBuilder.literal("destroy")
                        .then(LiteralBuilder.literal("all")
                                .executes((context, source, arguments) -> npcManager.destroyAll()))
                        .then(ArgumentBuilder.argument(ArcadiaArgumentType.NPC, "npc id")
                                .executes((context, source, arguments) -> npcManager.destroy(arguments.get(1, ArcadiaArgumentType.NPC).getId()))));
    }

    private void handleCreate(Player player, @NotNull Arguments arguments) {
        String name = arguments.get(1, ArgumentType.QUOTED_WORD);
        ArcadiaNpc npc;
        if (arguments.size() > 2) {
            UUID uuid = arguments.get(2, ArgumentType.UUID);
            npc = new ArcadiaNpc(player.getWorld(), player.getLocation(), name, uuid);
        } else {
            npc = new ArcadiaNpc(player.getWorld(), player.getLocation(), name);
        }
        plugin.getNpcManager().create(npc)
                .thenAccept(createdNpc -> plugin.getPlayerManager().getPlayerData(player).getSender().sendMessage("Successfully created an NPC with the id of " + createdNpc.getId()));
    }
}
