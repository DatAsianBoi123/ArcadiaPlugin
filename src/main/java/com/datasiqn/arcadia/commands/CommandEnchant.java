package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.commands.arguments.ArcadiaArgumentType;
import com.datasiqn.arcadia.enchants.EnchantType;
import com.datasiqn.arcadia.items.ArcadiaItem;
import com.datasiqn.arcadia.managers.PlayerManager;
import com.datasiqn.commandcore.arguments.ArgumentType;
import com.datasiqn.commandcore.commands.Command;
import com.datasiqn.commandcore.commands.builder.ArgumentBuilder;
import com.datasiqn.commandcore.commands.builder.CommandBuilder;
import com.datasiqn.commandcore.commands.builder.LiteralBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandEnchant {
    private final Arcadia plugin;

    public CommandEnchant(Arcadia plugin) {
        this.plugin = plugin;
    }

    public Command getCommand() {
        PlayerManager playerManager = plugin.getPlayerManager();
        return new CommandBuilder<>(Player.class)
                .permission(ArcadiaPermission.PERMISSION_USE_ENCHANT)
                .description("Enchants the item in your hand")
                .then(LiteralBuilder.<Player>literal("add")
                        .then(ArgumentBuilder.<Player, EnchantType>argument(ArcadiaArgumentType.ENCHANT, "enchant")
                                .then(ArgumentBuilder.<Player, Integer>argument(ArgumentType.NATURAL_NUMBER, "level")
                                        .executes(context -> {
                                            ItemStack itemStack = context.getSender().getInventory().getItemInMainHand();
                                            ArcadiaItem arcadiaItem = new ArcadiaItem(itemStack);
                                            EnchantType enchantType = context.parseArgument(ArcadiaArgumentType.ENCHANT, 1);
                                            int level = context.parseArgument(ArgumentType.NATURAL_NUMBER, 2);
                                            arcadiaItem.getItemMeta().addEnchant(enchantType, level);
                                            context.getSender().getInventory().setItemInMainHand(arcadiaItem.build());
                                            playerManager.getPlayerData(context.getSender()).getPlayer().sendMessage("Enchanted with " + enchantType.getEnchantment().getName() + " " + level);
                                        }))
                                .executes(context -> {
                                    ItemStack itemStack = context.getSender().getInventory().getItemInMainHand();
                                    ArcadiaItem arcadiaItem = new ArcadiaItem(itemStack);
                                    EnchantType enchantType = context.parseArgument(ArcadiaArgumentType.ENCHANT, 1);
                                    arcadiaItem.getItemMeta().addEnchant(enchantType, 1);
                                    context.getSender().getInventory().setItemInMainHand(arcadiaItem.build());
                                    playerManager.getPlayerData(context.getSender()).getPlayer().sendMessage("Enchanted with " + enchantType.getEnchantment().getName() + " 1");
                                }))
                )
                .then(LiteralBuilder.<Player>literal("remove")
                        .then(ArgumentBuilder.<Player, EnchantType>argument(ArcadiaArgumentType.ENCHANT, "enchant")
                                .executes(context -> {
                                    ItemStack itemStack = context.getSender().getInventory().getItemInMainHand();
                                    ArcadiaItem arcadiaItem = new ArcadiaItem(itemStack);
                                    EnchantType enchantType = context.parseArgument(ArcadiaArgumentType.ENCHANT, 1);
                                    if (!arcadiaItem.getItemMeta().hasEnchant(enchantType)) {
                                        playerManager.getPlayerData(context.getSender()).getPlayer().sendMessage("That item does not have that enchant");
                                        return;
                                    }
                                    arcadiaItem.getItemMeta().removeEnchant(enchantType);
                                    context.getSender().getInventory().setItemInMainHand(arcadiaItem.build());
                                    playerManager.getPlayerData(context.getSender()).getPlayer().sendMessage("Removed enchant " + enchantType.getEnchantment().getName());
                                }))
                        .executes(context -> {
                            ItemStack itemStack = context.getSender().getInventory().getItemInMainHand();
                            ArcadiaItem arcadiaItem = new ArcadiaItem(itemStack);
                            arcadiaItem.getItemMeta().clearEnchants();
                            context.getSender().getInventory().setItemInMainHand(arcadiaItem.build());
                            playerManager.getPlayerData(context.getSender()).getPlayer().sendMessage("Removed all enchants");
                        }))
                .build();
    }
}
