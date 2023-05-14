package com.datasiqn.arcadia;

import com.datasiqn.arcadia.commands.*;
import com.datasiqn.arcadia.events.*;
import com.datasiqn.arcadia.items.ArcadiaItem;
import com.datasiqn.arcadia.managers.DungeonManager;
import com.datasiqn.arcadia.managers.PlayerManager;
import com.datasiqn.arcadia.players.PlayerData;
import com.datasiqn.arcadia.util.ItemUtil;
import com.datasiqn.arcadia.util.PdcUtil;
import com.datasiqn.commandcore.CommandCore;
import com.datasiqn.commandcore.commands.builder.CommandBuilder;
import com.datasiqn.commandcore.managers.CommandManager;
import com.datasiqn.schedulebuilder.ScheduleBuilder;
import org.bukkit.*;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Arcadia extends JavaPlugin {
    private final PlayerManager playerManager = new PlayerManager(this);
    private final DungeonManager dungeonManager = new DungeonManager(this);

    private final long lastModified = getFile().lastModified();

    @Override
    public void onLoad() {
        PdcUtil.setPlugin(this);

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
        if (Bukkit.getWorld(DungeonManager.DEFAULT_DUNGEON_NAME) == null) {
            WorldCreator.name(DungeonManager.DEFAULT_DUNGEON_NAME).type(WorldType.FLAT).generateStructures(false).createWorld();
            getLogger().info("Created default dungeon");
        }

        CommandCore.init(this, "arcadia");

        registerAllCommands();
        registerAllListeners();

        setupConfig();
        loadPlayerData();

        setupDungeons();

        ScheduleBuilder.create()
                .repeatEvery(1).seconds()
                .executes(runnable -> {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        PlayerData playerData = playerManager.getPlayerData(player);
                        playerData.updateActionbar();

                        ItemStack itemStack = player.getInventory().getItemInMainHand();
                        if (itemStack.equals(playerData.getEquipment().getItemInMainHand().build())) return;
                        ArcadiaItem arcadiaItem = new ArcadiaItem(itemStack);
                        ItemStack newItem = arcadiaItem.build();
                        if (!newItem.equals(player.getInventory().getItemInMainHand()))
                            player.getInventory().setItemInMainHand(newItem);
                        playerData.getEquipment().setItemInMainHand(arcadiaItem);
                    }
                }).run(this);

        ScheduleBuilder.create()
                .repeatEvery(1).seconds()
                .executes(runnable -> {
                    if (getFile().lastModified() > lastModified) {
                        runnable.cancel();
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rl confirm");
                    }
                }).run(this);
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
        commandManager.registerCommand("bag", new CommandBuilder()
                .requiresPlayer()
                .executes(context -> {
                    ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
                    SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
                    if (meta == null) return;

                    meta.setDisplayName(ChatColor.WHITE + "Item Bag");
                    List<String> lore = new ArrayList<>();
                    lore.add(ChatColor.GRAY + "Holds all of your upgrades in this run");
                    lore.add("");
                    lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Click to open");
                    meta.setLore(lore);

                    ItemUtil.setHeadSkin(meta, "875e79488847ba02d5e12e7042d762e87ce08fa84fb89c35d6b5cccb8b9f4bed", UUID.randomUUID());
                    PersistentDataContainer pdc = meta.getPersistentDataContainer();
                    PdcUtil.set(pdc, ArcadiaTag.UPGRADE_BAG, true);
                    itemStack.setItemMeta(meta);
                    context.getSource().getPlayer().unwrap().getInventory().addItem(itemStack);
                }));
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
