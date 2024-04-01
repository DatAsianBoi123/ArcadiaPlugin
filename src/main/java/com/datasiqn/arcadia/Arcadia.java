package com.datasiqn.arcadia;

import com.datasiqn.arcadia.amulet.Amulet;
import com.datasiqn.arcadia.amulet.PowerStone;
import com.datasiqn.arcadia.commands.*;
import com.datasiqn.arcadia.commands.argument.ArcadiaArgumentType;
import com.datasiqn.arcadia.item.ArcadiaItem;
import com.datasiqn.arcadia.listeners.*;
import com.datasiqn.arcadia.managers.*;
import com.datasiqn.arcadia.player.PlayerData;
import com.datasiqn.arcadia.util.ItemUtil;
import com.datasiqn.arcadia.util.PdcUtil;
import com.datasiqn.commandcore.CommandCore;
import com.datasiqn.commandcore.InitOptions;
import com.datasiqn.commandcore.command.builder.CommandBuilder;
import com.datasiqn.commandcore.managers.ArgumentTypeManager;
import com.datasiqn.commandcore.managers.CommandManager;
import com.datasiqn.menuapi.MenuApi;
import com.datasiqn.schedulebuilder.ScheduleBuilder;
import org.bukkit.*;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Arcadia extends JavaPlugin {
    private final PlayerManager playerManager = new PlayerManager(this);
    private final DungeonManager dungeonManager = new DungeonManager(this);
    private final UpgradeEventManager upgradeEventManager = new UpgradeEventManager(this);
    private final ScoreboardManager scoreboardManager = new ScoreboardManager(this);
    private final AbilityCooldownManager cooldownManager = new AbilityCooldownManager();
    private final LevelRewardManager levelRewardManager = new LevelRewardManager();
    private final NpcManager npcManager = new NpcManager(this, new File(getDataFolder(), "npc-data.json"));
    private final MenuApi menuApi = MenuApi.getInstance();

    private final long lastModified = getFile().lastModified();

    @Override
    public void onLoad() {
        PdcUtil.setPlugin(this);

        if (new File(getDataFolder().getPath() + File.separatorChar + "player-data").mkdir()) {
            getLogger().info("Created player data folder");
        }

        PowerStone.addRewards(levelRewardManager);
        Amulet.addRewards(levelRewardManager);
    }

    @Override
    public void onEnable() {
        npcManager.load()
                .whenComplete((v, err) -> {
                    if (err == null) getLogger().info("Loaded NPC data");
                    else getLogger().severe("Could not load NPC data: " + err.getMessage());
                });

        if (Bukkit.getWorld(DungeonManager.DEFAULT_DUNGEON_NAME) == null) {
            WorldCreator.name(DungeonManager.DEFAULT_DUNGEON_NAME).type(WorldType.FLAT).generateStructures(false).createWorld();
            getLogger().info("Created default dungeon");
        }

        CommandCore.init(this, new InitOptions.Builder("arcadia")
                .warnOn(InitOptions.Warning.MISSING_PERMISSION, InitOptions.Warning.MISSING_DESCRIPTION)
                .build());

        registerArgumentTypes();
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
                        playerData.updateValues();
                        playerData.updateActionbar();
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

    @Override
    public void onDisable() {
        try {
            npcManager.save().get();
        } catch (InterruptedException e) {
            getLogger().severe("Could not save npc data: future was interrupted");
        } catch (ExecutionException e) {
            getLogger().severe("Could not save npc data: " + e.getMessage());
        }
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

    public UpgradeEventManager getUpgradeEventManager() {
        return upgradeEventManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public AbilityCooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public LevelRewardManager getLevelRewardManager() {
        return levelRewardManager;
    }

    public NpcManager getNpcManager() {
        return npcManager;
    }

    private void registerArgumentTypes() {
        ArgumentTypeManager manager = CommandCore.getInstance().getArgumentTypeManager();
        manager.register(ArcadiaArgumentType.PLAYER);
        manager.register(ArcadiaArgumentType.PLAYER_ATTRIBUTE);
        manager.register(ArcadiaArgumentType.ITEM);
        manager.register(ArcadiaArgumentType.LOOT_TABLE);
        manager.register(ArcadiaArgumentType.RECIPE);
        manager.register(ArcadiaArgumentType.UPGRADE);
        manager.register(ArcadiaArgumentType.ENTITY);
        manager.register(ArcadiaArgumentType.GUI);
        manager.register(ArcadiaArgumentType.DUNGEON);
        manager.register(ArcadiaArgumentType.NPC);
    }

    private void registerAllCommands() {
        CommandManager commandManager = CommandCore.getInstance().getCommandManager();
        commandManager.registerCommand(new CommandItem());
        commandManager.registerCommand(new CommandGUI(this));
        commandManager.registerCommand(new CommandSummon(this));
        commandManager.registerCommand(new CommandHeal(this));
        commandManager.registerCommand(new CommandDebug(this));
        commandManager.registerCommand(new CommandViewRecipe());
        commandManager.registerCommand(new CommandViewUpgrade());
        commandManager.registerCommand(new CommandLoot());
        commandManager.registerCommand(new CommandDungeons(this).getCommand());
        commandManager.registerCommand(new CommandLobby(this));
        commandManager.registerCommand(new CommandSpawn());
        commandManager.registerCommand(new CommandPlayerData(this).getCommand());
        commandManager.registerCommand(new CommandNpc(this).getCommand());
        commandManager.registerCommand(new CommandBuilder("bag")
                .requiresPlayer()
                .executes((context, source, arguments) -> {
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
                    source.getPlayer().getInventory().addItem(itemStack);
                }));
    }

    private void registerAllListeners() {
        menuApi.registerEvents(this);

        registerListener(new ItemListener(this));
        registerListener(new DamageListener(this));
        registerListener(new PlayerListener(this));
        registerListener(new GuiListener(this));
        registerListener(new ConsumableListener(this));
        registerListener(new UpgradeListener(this));
        registerListener(new LootTableListener(this));
    }

    private void setupConfig() {
        saveDefaultConfig();
        ConfigurationSerialization.registerClass(ArcadiaItem.class);
    }

    private void loadPlayerData() {
        // Load players asynchronously using a thread pool with 6 max threads
        ExecutorService threadPool = Executors.newFixedThreadPool(6);

        Bukkit.getOnlinePlayers().forEach(player -> threadPool.submit(() -> playerManager.getPlayerData(player).loadData()));
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
