package com.datasiqn.arcadia;

import com.datasiqn.arcadia.commands.*;
import com.datasiqn.arcadia.items.ArcadiaItem;
import com.datasiqn.arcadia.listeners.*;
import com.datasiqn.arcadia.managers.DungeonManager;
import com.datasiqn.arcadia.managers.PlayerManager;
import com.datasiqn.arcadia.managers.UpgradeEventManager;
import com.datasiqn.arcadia.players.PlayerData;
import com.datasiqn.arcadia.util.ItemUtil;
import com.datasiqn.arcadia.util.PdcUtil;
import com.datasiqn.commandcore.CommandCore;
import com.datasiqn.commandcore.commands.builder.CommandBuilder;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Arcadia extends JavaPlugin {
    private final PlayerManager playerManager = new PlayerManager(this);
    private final DungeonManager dungeonManager = new DungeonManager(this);
    private final UpgradeEventManager upgradeEventManager = new UpgradeEventManager(this);
    private final MenuApi menuApi = MenuApi.getInstance();

    private final long lastModified = getFile().lastModified();

    @Override
    public void onLoad() {
        PdcUtil.setPlugin(this);

        if (new File(getDataFolder().getPath() + File.separatorChar + "player-data").mkdir()) {
            getLogger().info("Created player data folder");
        }
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

    public UpgradeEventManager getUpgradeEventManager() {
        return upgradeEventManager;
    }

    private void registerAllCommands() {
        CommandManager commandManager = CommandCore.getInstance().getCommandManager();
        commandManager.registerCommand("i", new CommandItem().getCommand());
        commandManager.registerCommand("opengui", new CommandGUI(this).getCommand());
        commandManager.registerCommand("summon", new CommandSummon().getCommand());
        commandManager.registerCommand("heal", new CommandHeal(this).getCommand());
        commandManager.registerCommand("debug", new CommandDebug(this).getCommand());
        commandManager.registerCommand("viewrecipe", new CommandViewRecipe().getCommand());
        commandManager.registerCommand("viewupgrade", new CommandViewUpgrade().getCommand());
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
        menuApi.registerEvents(this);

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
