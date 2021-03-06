package de.deroq.nicksystem.game;

import de.deroq.database.models.DatabaseService;
import de.deroq.database.models.DatabaseServiceType;
import de.deroq.database.services.mongo.MongoDatabaseService;
import de.deroq.nicksystem.api.utils.APIInstanceUtil;
import de.deroq.nicksystem.game.commands.NickCommand;
import de.deroq.nicksystem.game.implementations.GameAPIImplementation;
import de.deroq.nicksystem.game.implementations.NickAPIImplementation;
import de.deroq.nicksystem.game.listeners.PlayerJoinListener;
import de.deroq.nicksystem.game.listeners.PlayerQuitListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author deroq
 * @since 10.07.2022
 */

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
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void registerListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerJoinListener(), this);
        pluginManager.registerEvents(new PlayerQuitListener(), this);
    }

    private void registerCommands() {
        getCommand("nick").setExecutor(new NickCommand());
    }

    public MongoDatabaseService getDatabaseService() {
        return databaseService;
    }
}
