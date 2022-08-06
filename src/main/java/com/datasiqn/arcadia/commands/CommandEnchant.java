package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.commands.arguments.ArgumentType;
import com.datasiqn.arcadia.commands.builder.ArgumentBuilder;
import com.datasiqn.arcadia.commands.builder.CommandBuilder;
import com.datasiqn.arcadia.commands.builder.LiteralBuilder;
import com.datasiqn.arcadia.enchants.EnchantType;
import com.datasiqn.arcadia.items.ArcadiaItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandEnchant {
    public ArcadiaCommand getCommand() {
        return new CommandBuilder<>(Player.class)
                .permission(ArcadiaPermission.PERMISSION_USE_ENCHANT)
                .description("Enchants the item in your hand")
                .then(LiteralBuilder.<Player>literal("add")
                        .then(ArgumentBuilder.<Player, EnchantType>argument(ArgumentType.ENCHANT, "enchant")
                                .then(ArgumentBuilder.<Player, Integer>argument(ArgumentType.INTEGER, "level")
                                        .executes(context -> {
                                            ItemStack itemStack = context.sender().get().getInventory().getItemInMainHand();
                                            ArcadiaItem arcadiaItem = new ArcadiaItem(itemStack);
                                            EnchantType enchantType = context.parseArgument(ArgumentType.ENCHANT, 1);
                                            int level = context.parseArgument(ArgumentType.INTEGER, 2);
                                            arcadiaItem.getItemMeta().addEnchant(enchantType, level);
                                            context.sender().get().getInventory().setItemInMainHand(arcadiaItem.build());
                                            context.sender().sendMessage("Enchanted with " + enchantType.getEnchantment().getName() + " " + level);
                                        }))
                                .executes(context -> {
                                    ItemStack itemStack = context.sender().get().getInventory().getItemInMainHand();
                                    ArcadiaItem arcadiaItem = new ArcadiaItem(itemStack);
                                    EnchantType enchantType = context.parseArgument(ArgumentType.ENCHANT, 1);
                                    arcadiaItem.getItemMeta().addEnchant(enchantType, 1);
                                    context.sender().get().getInventory().setItemInMainHand(arcadiaItem.build());
                                    context.sender().sendMessage("Enchanted with " + enchantType.getEnchantment().getName() + " 1");
                                }))
                )
                .then(LiteralBuilder.<Player>literal("remove")
                        .then(ArgumentBuilder.<Player, EnchantType>argument(ArgumentType.ENCHANT, "enchant")
                                .executes(context -> {
                                    ItemStack itemStack = context.sender().get().getInventory().getItemInMainHand();
                                    ArcadiaItem arcadiaItem = new ArcadiaItem(itemStack);
                                    EnchantType enchantType = context.parseArgument(ArgumentType.ENCHANT, 1);
                                    if (!arcadiaItem.getItemMeta().hasEnchant(enchantType)) {
                                        context.sender().sendMessage("That item does not have that enchant");
                                        return;
                                    }
                                    arcadiaItem.getItemMeta().removeEnchant(enchantType);
                                    context.sender().get().getInventory().setItemInMainHand(arcadiaItem.build());
                                    context.sender().sendMessage("Removed enchant " + enchantType.getEnchantment().getName());
                                }))
                        .executes(context -> {
                            ItemStack itemStack = context.sender().get().getInventory().getItemInMainHand();
                            ArcadiaItem arcadiaItem = new ArcadiaItem(itemStack);
                            arcadiaItem.getItemMeta().clearEnchants();
                            context.sender().get().getInventory().setItemInMainHand(arcadiaItem.build());
                            context.sender().sendMessage("Removed all enchants");
                        }))
                .build();
    }
}
