package com.datasiqn.arcadia;

import com.datasiqn.arcadia.commands.*;
import com.datasiqn.arcadia.commands.arguments.ArgumentType;
import com.datasiqn.arcadia.commands.arguments.Arguments;
import com.datasiqn.arcadia.commands.arguments.CommandArgumentType;
import com.datasiqn.arcadia.commands.builder.ArgumentBuilder;
import com.datasiqn.arcadia.commands.builder.CommandBuilder;
import com.datasiqn.arcadia.events.*;
import com.datasiqn.arcadia.items.ArcadiaItem;
import com.datasiqn.arcadia.managers.CommandManager;
import com.datasiqn.arcadia.managers.PlayerManager;
import com.datasiqn.arcadia.players.ArcadiaSender;
import com.datasiqn.arcadia.players.PlayerStats;
import com.datasiqn.arcadia.util.TabCompleteUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class Arcadia extends JavaPlugin implements CommandExecutor, TabCompleter {
    private static final Map<UUID, Boolean> DEBUG_MODE_MAP = new HashMap<>();

    private final PlayerManager playerManager = new PlayerManager(this);
    private final CommandManager commandManager = new CommandManager();

    private final long lastModified = getFile().lastModified();

    @Override
    public void onLoad() {
        // Man wtf even is this

//        Schema schema = DataFixers.getDataFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getCurrentVersion().getDataVersion().getVersion()));
//        TaggedChoice.TaggedChoiceType<?> choiceType = schema.findChoiceType(References.ENTITY_TREE);
//        Map<Object, Type<?>> types = (Map<Object, Type<?>>) choiceType.types();
//        types.put("minecraft:arcadia_dummy", types.get("minecraft:zombie"));
//
//        try {
//            Bukkit.getLogger().log(Level.INFO, "Set not frozen");
//            Field frozen = MappedRegistry.class.getDeclaredField("ca");
//            frozen.setAccessible(true);
//            frozen.set(Registry.ENTITY_TYPE, false);
//
//            Bukkit.getLogger().log(Level.INFO, "Reset intrusive holder cache");
//            Field intrusiveHolderCache = MappedRegistry.class.getDeclaredField("cc");
//            intrusiveHolderCache.setAccessible(true);
//            intrusiveHolderCache.set(Registry.ENTITY_TYPE, new IdentityHashMap<>());
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//
//        if (Registry.ENTITY_TYPE.getOptional(new ResourceLocation("arcadia_dummy")).isEmpty()) {
//            EntityType.Builder<EntityDummy.CustomEntity> zombie = EntityType.Builder.<EntityDummy.CustomEntity>of(EntityDummy::createEntity, MobCategory.AMBIENT)
//                    .sized(0.6f, 1.95f)
//                    .clientTrackingRange(8);
//            Bukkit.getLogger().log(Level.INFO, "Created entity type");
//            nmsEntityType = Registry.register(Registry.ENTITY_TYPE, "arcadia_dummy", zombie.build("arcadia_dummy"));
//        } else {
//            nmsEntityType = (EntityType<EntityDummy.CustomEntity>) Registry.ENTITY_TYPE.getOptional(new ResourceLocation("arcadia_dummy")).get();
//        }
//
//        Registry.ENTITY_TYPE.freeze();
    }

    @Override
    public void onEnable() {
        // Default Command
        PluginCommand command = this.getCommand("arcadia");
        assert command != null;
        command.setExecutor(this);
        command.setTabCompleter(this);

        // Custom Commands
        commandManager.registerCommand("help", new CommandBuilder<>(CommandSender.class)
                        .permission(ArcadiaPermission.PERMISSION_USE_HELP)
                        .description("Shows the help menu")
                        .then(ArgumentBuilder.argument(new CommandArgumentType(this), "command")
                                .executes(context -> sendCommandHelp(context.sender().get(), context.parseArgument(ArgumentType.STRING, 0))))
                        .executes(sender -> sendHelpMenu(sender.get()))
                        .build());
        commandManager.registerCommand("i", new CommandItem().getCommand());
        commandManager.registerCommand("opengui", new CommandGUI().getCommand());
        commandManager.registerCommand("summon", new CommandSummon().getCommand());
        commandManager.registerCommand("heal", new CommandHeal(this).getCommand());
        commandManager.registerCommand("debug", new CommandDebug(this).getCommand());
        commandManager.registerCommand("viewrecipe", new CommandViewRecipe().getCommand());
        commandManager.registerCommand("loot", new CommandLoot().getCommand());
        commandManager.registerCommand("enchant", new CommandEnchant().getCommand());

        // Listeners
        registerListener(new InventoryEvents());
        registerListener(new ItemEvents(this));
        registerListener(new DamageEvents(this));
        registerListener(new PlayerEvents(this));
        registerListener(new GUIEvents());

        // Auto Reload Server
        new BukkitRunnable() {
            public void run() {
                if (getFile().lastModified() > lastModified) {
                    cancel();
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rl confirm");
                }
            }
        }.runTaskTimer(this, 0, 20);

        // Update Players
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                PlayerStats itemStats = playerManager.getPlayerData(p).playerStats();
                itemStats.updateValues();
                itemStats.updateActionbar();

                ItemStack itemStack = p.getInventory().getItemInMainHand();
                if (itemStack.getType().isAir()) return;
                ItemStack newItem = new ArcadiaItem(itemStack).build();
                if (!newItem.equals(p.getInventory().getItemInMainHand())) p.getInventory().setItemInMainHand(newItem);
            }
        }, 0, 20);
    }

    public void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (args.length >= 1) {
            ArcadiaCommand cmd = commandManager.getCommand(args[0]);
            if (cmd == null) return false;
            if (cmd.getPermissionString() != null && !sender.hasPermission(cmd.getPermissionString())) {
                sender.sendMessage(ArcadiaPermission.MISSING_PERMISSIONS);
                return true;
            }
            List<String> listArgs = new ArrayList<>(Arrays.asList(args));
            listArgs.remove(0);
            if (!cmd.execute(new ArcadiaSender<>(this, sender), new Arguments(listArgs))) {
                sender.sendMessage(ChatColor.RED + "Incorrect usage!",
                        ChatColor.GRAY + "Usage(s):");
                cmd.getUsages().forEach(usage -> sender.sendMessage(ChatColor.BLUE + " /arcadia " + args[0] + " " + usage));
            }
            return true;
        }

        if (!sender.hasPermission(ArcadiaPermission.PERMISSION_USE_HELP)) {
            sender.sendMessage(ArcadiaPermission.MISSING_PERMISSIONS);
            return true;
        }
        sendHelpMenu(sender);
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String @NotNull [] args) {
        if (args.length == 1) {
            List<String> tabComplete = new ArrayList<>();
            commandManager.allCommands().forEach((s, command1) -> {
                if (command1.getPermissionString() == null || sender.hasPermission(command1.getPermissionString())) tabComplete.add(s);
            });
            TabCompleteUtil.filterStartsWith(tabComplete, args[0]);
            TabCompleteUtil.sort(tabComplete);
            return tabComplete;
        } else {
            ArcadiaCommand cmd = commandManager.getCommand(args[0]);
            if (cmd == null || (cmd.getPermissionString() != null && !sender.hasPermission(cmd.getPermissionString()))) return new ArrayList<>();
            List<String> listArgs = new ArrayList<>(Arrays.asList(args));
            listArgs.remove(0);
            List<String> tabComplete = cmd.tabComplete(new ArcadiaSender<>(this, sender), new Arguments(listArgs));
            TabCompleteUtil.filterStartsWith(tabComplete, args[args.length - 1]);
            TabCompleteUtil.sort(tabComplete);
            return tabComplete;
        }
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public void sendMessage(String message, @NotNull CommandSender sender) {
        sender.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "Arcadia" + ChatColor.GOLD + " > " + ChatColor.RESET + message);
    }

    public void sendHelpMenu(@NotNull CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "Arcadia Commands");
        commandManager.allCommands().keySet().stream().sorted().forEach(name -> {
            ArcadiaCommand cmd = commandManager.getCommand(name);
            if (cmd.getPermissionString() == null || sender.hasPermission(cmd.getPermissionString())) sender.sendMessage(ChatColor.BLUE + " " + name, ChatColor.GRAY + "  ↳ " + cmd.getDescription());
        });
    }

    public void sendCommandHelp(@NotNull CommandSender sender, @NotNull String commandName) {
        if (!commandManager.hasCommand(commandName)) throw new RuntimeException("Command " + commandName + " does not exist");
        ArcadiaCommand command = commandManager.getCommand(commandName);
        sender.sendMessage(ChatColor.GOLD + "Command " + commandName,
                ChatColor.GRAY + " Description: " + ChatColor.WHITE + command.getDescription(),
                ChatColor.GRAY + " Usage(s):");
        command.getUsages().forEach(usage -> sender.sendMessage(ChatColor.BLUE + "  /arcadia " + commandName + " " + usage));
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public void runAfterOneTick(@NotNull Runnable runnable) {
        Bukkit.getScheduler().runTask(this, runnable);
    }

    public void setDebugMode(UUID uuid, boolean debugMode) {
        DEBUG_MODE_MAP.put(uuid, debugMode);
    }

    public boolean inDebugMode(UUID uuid) {
        return DEBUG_MODE_MAP.getOrDefault(uuid, false);
    }

    @Contract("_ -> new")
    public static @NotNull NamespacedKey getNK(String key) {
        return new NamespacedKey(Arcadia.getPlugin(Arcadia.class), key);
    }
}
