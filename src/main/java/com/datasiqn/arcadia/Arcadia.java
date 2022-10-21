package com.datasiqn.arcadia;

import com.datasiqn.arcadia.commands.*;
import com.datasiqn.arcadia.commands.arguments.ArcadiaArgumentType;
import com.datasiqn.arcadia.dungeons.DungeonInstance;
import com.datasiqn.arcadia.events.*;
import com.datasiqn.arcadia.items.ArcadiaItem;
import com.datasiqn.arcadia.loottables.LootChestLootTable;
import com.datasiqn.arcadia.managers.DungeonManager;
import com.datasiqn.arcadia.managers.PlayerManager;
import com.datasiqn.arcadia.players.ArcadiaSender;
import com.datasiqn.arcadia.players.PlayerData;
import com.datasiqn.arcadia.util.ItemUtil;
import com.datasiqn.commandcore.CommandCore;
import com.datasiqn.commandcore.commands.builder.ArgumentBuilder;
import com.datasiqn.commandcore.commands.builder.CommandBuilder;
import com.datasiqn.commandcore.commands.builder.LiteralBuilder;
import com.datasiqn.commandcore.managers.CommandManager;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.block.EnderChest;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class Arcadia extends JavaPlugin implements CommandExecutor, TabCompleter {
    private static final Map<UUID, Boolean> DEBUG_MODE_MAP = new HashMap<>();

    private final PlayerManager playerManager = new PlayerManager(this);
    private final DungeonManager dungeonManager = new DungeonManager();

    private final long lastModified = getFile().lastModified();

    @Override
    public void onLoad() {
        if (new File(getDataFolder().getPath() + File.separatorChar + "player-data").mkdir()) {
            getLogger().info("Created player data folder");
        }

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
        CommandCore commandCore = CommandCore.init(this, "arcadia");

        // Custom Commands
        CommandManager commandManager = commandCore.getCommandManager();
        commandManager.registerCommand("i", new CommandItem().getCommand());
        commandManager.registerCommand("opengui", new CommandGUI(this).getCommand());
        commandManager.registerCommand("summon", new CommandSummon().getCommand());
        commandManager.registerCommand("heal", new CommandHeal(this).getCommand());
        commandManager.registerCommand("debug", new CommandDebug(this).getCommand());
        commandManager.registerCommand("config", new CommandBuilder<>(CommandSender.class)
                .permission(ArcadiaPermission.PERMISSION_USE_CONFIG)
                .then(LiteralBuilder.literal("reload")
                        .executes(context -> {
                            reloadConfig();
                            new ArcadiaSender<>(context.getSender()).sendMessage("Successfully reloaded the config");
                        }))
                .build());
        commandManager.registerCommand("viewrecipe", new CommandViewRecipe().getCommand());
        commandManager.registerCommand("loot", new CommandLoot().getCommand());
        commandManager.registerCommand("enchant", new CommandEnchant(this).getCommand());
        commandManager.registerCommand("dungeons", new CommandBuilder<>(Player.class)
                .permission(ArcadiaPermission.PERMISSION_MANAGE_DUNGEONS)
                .description("Manages different dungeon instances")
                .then(LiteralBuilder.<Player>literal("create")
                        .executes(context -> {
                            ArcadiaSender<Player> player = playerManager.getPlayerData(context.getSender()).getPlayer();
                            DungeonInstance instance = dungeonManager.createDungeon();
                            if (instance == null) {
                                player.sendError("An unexpected error occurred. Please try again later");
                                return;
                            }
                            player.sendMessage("Successfully created a new dungeon with the id of " + instance.id());
                        }))
                .then(LiteralBuilder.<Player>literal("delete")
                        .then(ArgumentBuilder.<Player, DungeonInstance>argument(ArcadiaArgumentType.DUNGEON, "world name")
                                .executes(context -> {
                                    DungeonInstance instance = context.parseArgument(ArcadiaArgumentType.DUNGEON, 1);
                                    ArcadiaSender<Player> player = playerManager.getPlayerData(context.getSender()).getPlayer();
                                    if (!dungeonManager.deleteDungeon(instance)) {
                                        player.sendError("An error occurred when deleting the world. Please try again later");
                                        return;
                                    }
                                    player.sendMessage("Successfully deleted the dungeon " + instance.id());
                                })))
                .then(LiteralBuilder.<Player>literal("tp")
                        .then(ArgumentBuilder.<Player, DungeonInstance>argument(ArcadiaArgumentType.DUNGEON, "dungeon id")
                                .executes(context -> dungeonManager.joinDungeon(context.getSender(), context.parseArgument(ArcadiaArgumentType.DUNGEON, 1)))))
                .build());
        commandManager.registerCommand("lobby", new CommandBuilder<>(Player.class)
                .permission(ArcadiaPermission.PERMISSION_USE_LOBBY)
                .description("Sends you to the lobby")
                .executes(dungeonManager::leaveDungeon)
                .build());
        commandManager.registerCommand("spawn", new CommandBuilder<>(Player.class)
                .then(LiteralBuilder.<Player>literal("upgradechest")
                        .executes(context -> {
                            Player player = context.getSender();
                            Location location = player.getLocation();
                            World world = player.getWorld();
                            world.setType(location, Material.ENDER_CHEST);
                            EnderChest enderChest = (EnderChest) world.getBlockAt(location).getState();
                            enderChest.getPersistentDataContainer().set(ArcadiaKeys.UPGRADE_CHEST, PersistentDataType.BYTE, (byte) 1);
                            enderChest.update();
                        }))
                .then(LiteralBuilder.<Player>literal("lootchest")
                        .executes(context -> {
                            Player player = context.getSender();
                            Location location = player.getLocation();
                            World world = player.getWorld();
                            world.setType(location, Material.CHEST);
                            Chest chest = (Chest) world.getBlockAt(location).getState();
                            chest.setLootTable(new LootChestLootTable());
                            chest.update();
                            System.out.println(chest.getLootTable());
                        }))
                .build());
        commandManager.registerCommand("bag", new CommandBuilder<>(Player.class)
                .executes(sender -> {
                    ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
                    SkullMeta itemMeta = (SkullMeta) itemStack.getItemMeta();
                    ItemUtil.setHeadSkin(itemMeta, "875e79488847ba02d5e12e7042d762e87ce08fa84fb89c35d6b5cccb8b9f4bed", UUID.randomUUID());
                    itemStack.setItemMeta(itemMeta);
                    sender.getInventory().addItem(itemStack);
                })
                .build());
        commandManager.registerCommand("test", new CommandBuilder<>(CommandSender.class)
                .executes(sender -> {
                    sender.sendMessage("I am waiting a few seconds, but the main thread isn't blocked!");
                    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    future.thenAccept(v -> sender.sendMessage("Future has been completed!"));
                })
                .build());

        // Listeners
        registerListener(new InventoryEvents());
        registerListener(new ItemEvents(this));
        registerListener(new DamageEvents(this));
        registerListener(new PlayerEvents(this));
        registerListener(new GUIEvents(this));
        registerListener(new ConsumableEvents(this));
        registerListener(new UpgradeEvents(this));
        registerListener(new LootTableEvents(this));

        // Config stuff
        saveDefaultConfig();
        ConfigurationSerialization.registerClass(ArcadiaItem.class);

        Bukkit.getOnlinePlayers().forEach(player -> playerManager.getPlayerData(player).loadData());

        // Load dungeons from server files
        dungeonManager.loadDungeonsFromDisk();

        // Kick everyone from a dungeon (if they're in one)
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.getWorld().getName().startsWith(DungeonManager.DUNGEON_WORLD_PREFIX)) continue;
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        }

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
            for (Player player : Bukkit.getOnlinePlayers()) {
                PlayerData playerData = playerManager.getPlayerData(player);
                playerData.updateActionbar();

                ItemStack itemStack = player.getInventory().getItemInMainHand();
                if (itemStack.equals(playerData.getEquipment().getItemInMainHand().build())) return;
                ArcadiaItem arcadiaItem = new ArcadiaItem(itemStack);
                ItemStack newItem = arcadiaItem.build();
                if (!newItem.equals(player.getInventory().getItemInMainHand())) player.getInventory().setItemInMainHand(newItem);
                playerData.getEquipment().setItemInMainHand(arcadiaItem);
            }
        }, 0, 20);
    }

    public void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public DungeonManager getDungeonManager() {
        return dungeonManager;
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
