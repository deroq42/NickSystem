package de.deroq.nicksystem.game;

import de.deroq.database.models.DatabaseService;
import de.deroq.database.models.DatabaseServiceType;
import de.deroq.database.services.mongo.MongoDatabaseService;
import de.deroq.nicksystem.api.utils.APIInstanceUtil;
import de.deroq.nicksystem.game.commands.NickCommand;
import de.deroq.nicksystem.game.implementations.GameAPIImplementation;
import de.deroq.nicksystem.game.implementations.NickAPIImplementation;
import de.deroq.nicksystem.game.listeners.PlayerDeathListener;
import de.deroq.nicksystem.game.listeners.PlayerJoinListener;
import de.deroq.nicksystem.game.listeners.PlayerQuitListener;
import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class NickSystemGame extends JavaPlugin {

    private MongoDatabaseService databaseService;

    @Override
    public void onEnable() {
        initDatabase();
        initImplementations();
        registerListeners();
        registerCommands();

        getLogger().info("NickSystemLobby has been enabled.");
    }

    @Override
    public void onDisable() {

        getLogger().info("NickSystemLobby has been disabled.");
    }

    private void initDatabase() {
        this.databaseService = (MongoDatabaseService) new DatabaseService.builder(DatabaseServiceType.MONGO)
                .setHost("localhost")
                .setDatabase("nicksystem")
                .setUsername("root")
                .setPassword("")
                .setPort(27017)
                .build();

        databaseService.connect();
    }

    private void initImplementations() {
        try {
            NickAPIImplementation nickAPIImplementation = new NickAPIImplementation(this);
            APIInstanceUtil.setNickAPI(nickAPIImplementation);

            GameAPIImplementation gameAPIImplementation = new GameAPIImplementation(this);
            APIInstanceUtil.setGameAPI(gameAPIImplementation);
        }catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void registerListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerJoinListener(), this);
        pluginManager.registerEvents(new PlayerQuitListener(), this);
        pluginManager.registerEvents(new PlayerDeathListener(), this);
    }

    private void registerCommands() {
        SimpleCommandMap commandMap = ((CraftServer) Bukkit.getServer()).getCommandMap();
        commandMap.register("nick", new NickCommand("nick"));
    }

    public MongoDatabaseService getDatabaseService() {
        return databaseService;
    }
}
