package com.datasiqn.arcadia;

import com.datasiqn.arcadia.commands.*;
import com.datasiqn.arcadia.events.*;
import com.datasiqn.arcadia.items.ArcadiaItem;
import com.datasiqn.arcadia.managers.DungeonManager;
import com.datasiqn.arcadia.managers.PlayerManager;
import com.datasiqn.arcadia.players.PlayerData;
import com.datasiqn.arcadia.util.ItemUtil;
import com.datasiqn.commandcore.CommandCore;
import com.datasiqn.commandcore.commands.builder.CommandBuilder;
import com.datasiqn.commandcore.managers.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.UUID;

public final class Arcadia extends JavaPlugin {
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
        CommandCore.init(this, "arcadia");

        registerAllCommands();
        registerAllListeners();

        setupConfig();
        loadPlayerData();

        setupDungeons();

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

        new BukkitRunnable() {
            public void run() {
                if (getFile().lastModified() > lastModified) {
                    cancel();
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rl confirm");
                }
            }
        }.runTaskTimer(this, 0, 20);
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

    @Contract("_ -> new")
    public static @NotNull NamespacedKey getNK(String key) {
        return new NamespacedKey(Arcadia.getPlugin(Arcadia.class), key);
    }

    private void registerAllCommands() {
        CommandManager commandManager = CommandCore.getInstance().getCommandManager();
        commandManager.registerCommand("i", new CommandItem().getCommand());
        commandManager.registerCommand("opengui", new CommandGUI(this).getCommand());
        commandManager.registerCommand("summon", new CommandSummon().getCommand());
        commandManager.registerCommand("heal", new CommandHeal(this).getCommand());
        commandManager.registerCommand("debug", new CommandDebug(this).getCommand());
        commandManager.registerCommand("viewrecipe", new CommandViewRecipe().getCommand());
        commandManager.registerCommand("loot", new CommandLoot().getCommand());
        commandManager.registerCommand("enchant", new CommandEnchant(this).getCommand());
        commandManager.registerCommand("dungeons", new CommandDungeons(this).getCommand());
        commandManager.registerCommand("lobby", new CommandLobby(this).getCommand());
        commandManager.registerCommand("spawn", new CommandSpawn().getCommand());
        commandManager.registerCommand("bag", new CommandBuilder<>(Player.class)
                .executes(sender -> {
                    ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
                    SkullMeta itemMeta = (SkullMeta) itemStack.getItemMeta();
                    ItemUtil.setHeadSkin(itemMeta, "875e79488847ba02d5e12e7042d762e87ce08fa84fb89c35d6b5cccb8b9f4bed", UUID.randomUUID());
                    itemStack.setItemMeta(itemMeta);
                    sender.getInventory().addItem(itemStack);
                })
                .build());
    }

    private void registerAllListeners() {
        registerListener(new InventoryEvents());
        registerListener(new ItemEvents(this));
        registerListener(new DamageEvents(this));
        registerListener(new PlayerEvents(this));
        registerListener(new GUIEvents(this));
        registerListener(new ConsumableEvents(this));
        registerListener(new UpgradeEvents(this));
        registerListener(new LootTableEvents(this));
    }

    private void setupConfig() {
        saveDefaultConfig();
        ConfigurationSerialization.registerClass(ArcadiaItem.class);
    }

    private void loadPlayerData() {
        Bukkit.getOnlinePlayers().forEach(player -> playerManager.getPlayerData(player).loadData());
    }

    private void setupDungeons() {
        dungeonManager.loadDungeonsFromDisk();

        // Kick everyone from a dungeon (if they're in one)
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.getWorld().getName().startsWith(DungeonManager.DUNGEON_WORLD_PREFIX)) continue;
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        }
    }
}
