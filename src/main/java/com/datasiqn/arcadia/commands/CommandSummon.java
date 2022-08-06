package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.commands.arguments.ArgumentType;
import com.datasiqn.arcadia.commands.builder.ArgumentBuilder;
import com.datasiqn.arcadia.commands.builder.CommandBuilder;
import com.datasiqn.arcadia.entities.EntityType;
import org.bukkit.entity.Player;

public class CommandSummon {
    public ArcadiaCommand getCommand() {
        return new CommandBuilder<>(Player.class)
                .permission(ArcadiaPermission.PERMISSION_USE_SUMMON)
                .description("Summons a custom Arcadia Entity")
                .then(ArgumentBuilder.<Player, EntityType>argument(ArgumentType.ENTITY, "entity")
                        .executes(context -> context.parseArgument(ArgumentType.ENTITY, 0).getEntity().summonEntity(context.sender().get().getLocation())))
                .build();
    }
}
