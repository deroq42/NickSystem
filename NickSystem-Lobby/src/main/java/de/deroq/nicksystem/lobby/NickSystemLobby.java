package de.deroq.nicksystem.lobby;

import de.deroq.database.models.DatabaseService;
import de.deroq.database.models.DatabaseServiceType;
import de.deroq.database.services.mongo.MongoDatabaseService;
import de.deroq.nicksystem.api.utils.APIInstanceUtil;
import de.deroq.nicksystem.lobby.implementations.LobbyAPIImplementation;
import de.deroq.nicksystem.lobby.implementations.NickAPIImplementation;
import de.deroq.nicksystem.lobby.listeners.PlayerInteractListener;
import de.deroq.nicksystem.lobby.listeners.PlayerJoinListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author deroq
 * @since 10.07.2022
 */

public class NickSystemLobby extends JavaPlugin {

    private MongoDatabaseService databaseService;

    @Override
    public void onEnable() {
        initDatabase();
        initImplementations();
        registerListeners();

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

            LobbyAPIImplementation lobbyAPIImplementation = new LobbyAPIImplementation();
            APIInstanceUtil.setLobbyAPI(lobbyAPIImplementation);
        }catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void registerListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerJoinListener(), this);
        pluginManager.registerEvents(new PlayerInteractListener(), this);
    }

    public MongoDatabaseService getDatabaseService() {
        return databaseService;
    }

}
