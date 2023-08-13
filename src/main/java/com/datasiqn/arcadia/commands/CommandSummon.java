package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.commands.argument.ArcadiaArgumentType;
import com.datasiqn.commandcore.command.builder.ArgumentBuilder;
import com.datasiqn.commandcore.command.builder.CommandBuilder;

public class CommandSummon {
    public CommandBuilder getCommand() {
        return new CommandBuilder("summon")
                .permission(ArcadiaPermission.PERMISSION_USE_SUMMON)
                .description("Summons a custom Arcadia Entity")
                .then(ArgumentBuilder.argument(ArcadiaArgumentType.ENTITY, "entity")
                        .requiresPlayer()
                        .executes(context -> context.getArguments().get(0, ArcadiaArgumentType.ENTITY).getSummoner().summonEntity(context.getSource().getPlayer().getLocation())));
    }
}
