package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.commands.arguments.ArcadiaArgumentType;
import com.datasiqn.arcadia.entities.EntityType;
import com.datasiqn.commandcore.commands.Command;
import com.datasiqn.commandcore.commands.builder.ArgumentBuilder;
import com.datasiqn.commandcore.commands.builder.CommandBuilder;
import org.bukkit.entity.Player;

public class CommandSummon {
    public Command getCommand() {
        return new CommandBuilder<>(Player.class)
                .permission(ArcadiaPermission.PERMISSION_USE_SUMMON)
                .description("Summons a custom Arcadia Entity")
                .then(ArgumentBuilder.<Player, EntityType>argument(ArcadiaArgumentType.ENTITY, "entity")
                        .executes(context -> context.parseArgument(ArcadiaArgumentType.ENTITY, 0).getSummoner().summonEntity(context.getSender().getLocation())))
                .build();
    }
}
