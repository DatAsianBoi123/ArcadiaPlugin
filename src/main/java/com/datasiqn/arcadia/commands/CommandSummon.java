package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.entities.EntityType;
import com.datasiqn.commandcore.command.annotation.AnnotationCommand;
import com.datasiqn.commandcore.command.annotation.Argument;
import com.datasiqn.commandcore.command.annotation.CommandDescription;
import com.datasiqn.commandcore.command.annotation.Executor;
import com.datasiqn.commandcore.locatable.LocatableCommandSender;

@CommandDescription(name = "summon", description = "Summons a custom Arcadia Entity", permission = ArcadiaPermission.PERMISSION_USE_SUMMON)
public class CommandSummon implements AnnotationCommand {
    private final Arcadia plugin;

    public CommandSummon(Arcadia plugin) {
        this.plugin = plugin;
    }

    @Executor
    public void summon(LocatableCommandSender locatable,
                       @Argument(name = "entity") EntityType entityType) {
        entityType.getSummoner().summonEntity(locatable.getLocation(), plugin);
    }
}
