package com.datasiqn.arcadia.commands;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.ArcadiaPermission;
import com.datasiqn.arcadia.commands.argument.ArcadiaArgumentType;
import com.datasiqn.arcadia.enchants.EnchantType;
import com.datasiqn.arcadia.item.ArcadiaItem;
import com.datasiqn.arcadia.managers.PlayerManager;
import com.datasiqn.commandcore.argument.ArgumentType;
import com.datasiqn.commandcore.command.builder.ArgumentBuilder;
import com.datasiqn.commandcore.command.builder.CommandBuilder;
import com.datasiqn.commandcore.command.builder.LiteralBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class CommandEnchant {
    private final Arcadia plugin;

    public CommandEnchant(Arcadia plugin) {
        this.plugin = plugin;
    }

    public CommandBuilder getCommand() {
        PlayerManager playerManager = plugin.getPlayerManager();
        return new CommandBuilder()
                .permission(ArcadiaPermission.PERMISSION_USE_ENCHANT)
                .description("Enchants the item in your hand")
                .then(LiteralBuilder.literal("add")
                        .then(ArgumentBuilder.argument(ArcadiaArgumentType.ENCHANT, "enchant")
                                .then(ArgumentBuilder.argument(ArgumentType.NATURAL_NUMBER, "level")
                                        .requiresPlayer()
                                        .executes(context -> addEnchant(context.getSource().getPlayer().unwrap(), context.getArguments().get(1, ArcadiaArgumentType.ENCHANT).unwrap(), context.getArguments().get(2, ArgumentType.NATURAL_NUMBER).unwrap())))
                                .requiresPlayer()
                                .executes(context -> addEnchant(context.getSource().getPlayer().unwrap(), context.getArguments().get(1, ArcadiaArgumentType.ENCHANT).unwrap(), 1))))
                .then(LiteralBuilder.literal("remove")
                        .then(ArgumentBuilder.argument(ArcadiaArgumentType.ENCHANT, "enchant")
                                .requiresPlayer()
                                .executes(context -> {
                                    Player player = context.getSource().getPlayer().unwrap();
                                    PlayerInventory inventory = player.getInventory();
                                    ItemStack itemStack = inventory.getItemInMainHand();
                                    ArcadiaItem arcadiaItem = new ArcadiaItem(itemStack);
                                    EnchantType enchantType = context.getArguments().get(1, ArcadiaArgumentType.ENCHANT).unwrap();
                                    if (!arcadiaItem.getItemMeta().hasEnchant(enchantType)) {
                                        playerManager.getPlayerData(player).getSender().sendMessage("That item does not have that enchant");
                                        return;
                                    }
                                    arcadiaItem.getItemMeta().removeEnchant(enchantType);
                                    inventory.setItemInMainHand(arcadiaItem.build());
                                    playerManager.getPlayerData(player).getSender().sendMessage("Removed enchant " + enchantType.getEnchantment().getName());
                                }))
                        .requiresPlayer()
                        .executes(context -> {
                            Player player = context.getSource().getPlayer().unwrap();
                            PlayerInventory inventory = player.getInventory();
                            ItemStack itemStack = inventory.getItemInMainHand();
                            ArcadiaItem arcadiaItem = new ArcadiaItem(itemStack);
                            arcadiaItem.getItemMeta().clearEnchants();
                            inventory.setItemInMainHand(arcadiaItem.build());
                            playerManager.getPlayerData(player).getSender().sendMessage("Removed all enchants");
                        }));
    }

    private void addEnchant(Player player, EnchantType type, int level) {
        PlayerInventory inventory = player.getInventory();
        ItemStack itemStack = inventory.getItemInMainHand();
        ArcadiaItem arcadiaItem = new ArcadiaItem(itemStack);
        arcadiaItem.getItemMeta().addEnchant(type, level);
        inventory.setItemInMainHand(arcadiaItem.build());
        plugin.getPlayerManager().getPlayerData(player).getSender().sendMessage("Enchanted with " + type.getEnchantment().getName() + " " + level);
    }
}
