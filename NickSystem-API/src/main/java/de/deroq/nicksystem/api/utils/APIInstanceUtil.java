package de.deroq.nicksystem.api.utils;

import de.deroq.nicksystem.api.GameAPI;
import de.deroq.nicksystem.api.LobbyAPI;
import de.deroq.nicksystem.api.NickAPI;
import de.deroq.nicksystem.api.NickSystem;

import java.lang.reflect.Field;

/**
 * @author deroq
 * @since 10.07.2022
 */

public class APIInstanceUtil {

    public static void setNickAPI(NickAPI nickAPI) throws NoSuchFieldException, IllegalAccessException {
        Field field = NickSystem.class.getDeclaredField("nickAPI");
        field.setAccessible(true);
        field.set(null, nickAPI);
    }

    public static void setLobbyAPI(LobbyAPI lobbyAPI) throws NoSuchFieldException, IllegalAccessException {
        Field field = NickSystem.class.getDeclaredField("lobbyAPI");
        field.setAccessible(true);
        field.set(null, lobbyAPI);
    }

    public static void setGameAPI(GameAPI gameAPI) throws NoSuchFieldException, IllegalAccessException {
        Field field = NickSystem.class.getDeclaredField("gameAPI");
        field.setAccessible(true);
        field.set(null, gameAPI);
    }
}
