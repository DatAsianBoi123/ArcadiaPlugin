package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.commands.argument.ArcadiaArgumentType;
import com.datasiqn.arcadia.managers.NpcManager;
import com.datasiqn.arcadia.npc.ArcadiaNpc;
import com.datasiqn.arcadia.npc.CreatedNpc;
import com.datasiqn.arcadia.npc.SkinData;
import com.datasiqn.commandcore.argument.Arguments;
import com.datasiqn.commandcore.argument.type.ArgumentType;
import com.datasiqn.commandcore.command.builder.ArgumentBuilder;
import com.datasiqn.commandcore.command.builder.CommandBuilder;
import com.datasiqn.commandcore.command.builder.LiteralBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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
                .then(LiteralBuilder.literal("save")
                        .executes((context, source, arguments) -> npcManager.save()
                                .whenComplete((v, err) -> {
                                    if (err == null) source.sendMessage("Successfully saved NPC data");
                                    else plugin.getLogger().severe("Could not save NPC data: " + err.getMessage());
                                })))
                .then(LiteralBuilder.literal("load")
                        .executes((context, source, arguments) -> npcManager.load()
                                .whenComplete((v, err) -> {
                                    if (err == null) source.sendMessage("Successfully loaded NPC data");
                                    else plugin.getLogger().severe("Could not load NPC data: " + err.getMessage());
                                })))
                .then(LiteralBuilder.literal("create")
                        .then(ArgumentBuilder.argument(ArgumentType.QUOTED_WORD, "npc name")
                                .then(ArgumentBuilder.argument(ArgumentType.UUID, "skin uuid")
                                        .requiresPlayer()
                                        .executes((context, source, arguments) -> handleCreate(source.getPlayer(), arguments)))
                                .executes((context, source, arguments) -> handleCreate(source.getPlayer(), arguments))))
                .then(LiteralBuilder.literal("show")
                        .executes((context, source, arguments) -> {
                            for (long id : npcManager.ids()) {
                                CreatedNpc npc = npcManager.getNpc(id);
                                npc.show();
                            }
                            npcManager.updateVisibilities();
                        })
                        .then(ArgumentBuilder.argument(ArcadiaArgumentType.NPC, "npc id")
                                .executes((context, source, arguments) -> {
                                    CreatedNpc npc = arguments.get(1, ArcadiaArgumentType.NPC);
                                    npc.show();
                                    npcManager.updateVisibility(npc.getId());
                                })))
                .then(LiteralBuilder.literal("hide")
                        .executes((context, source, arguments) -> {
                            for (long id : npcManager.ids()) {
                                CreatedNpc npc = npcManager.getNpc(id);
                                npc.hide();
                            }
                            npcManager.updateVisibilities();
                        })
                        .then(ArgumentBuilder.argument(ArcadiaArgumentType.NPC, "npc id")
                                .executes((context, source, arguments) -> {
                                    CreatedNpc npc = arguments.get(1, ArcadiaArgumentType.NPC);
                                    npc.hide();
                                    npcManager.updateVisibility(npc.getId());
                                })))
                .then(LiteralBuilder.literal("destroy")
                            .executes((context, source, arguments) -> npcManager.destroyAll())
                        .then(ArgumentBuilder.argument(ArcadiaArgumentType.NPC, "npc id")
                                .executes((context, source, arguments) -> npcManager.destroy(arguments.get(1, ArcadiaArgumentType.NPC).getId()))));
    }

    private void handleCreate(Player player, @NotNull Arguments arguments) {
        String name = arguments.get(1, ArgumentType.QUOTED_WORD);
        SkinData skinData;
        if (arguments.size() > 2) skinData = SkinData.uuid(arguments.get(2, ArgumentType.UUID));
        else skinData = SkinData.none();
        ArcadiaNpc npc = new ArcadiaNpc(player.getWorld(), player.getLocation(), name, skinData);
        plugin.getNpcManager().create(npc)
                .thenAccept(createdNpc -> plugin.getPlayerManager().getPlayerData(player).getSender().sendMessage("Successfully created an NPC with the id of " + createdNpc.getId()));
    }
}
