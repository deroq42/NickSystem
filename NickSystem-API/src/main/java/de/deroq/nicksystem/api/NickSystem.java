package de.deroq.nicksystem.api;

public class NickSystem {

    private static NickAPI nickAPI;
    private static LobbyAPI lobbyAPI;
    private static GameAPI gameAPI;

    /* NickAPI. */
    public static NickAPI getNickAPI() {
        return nickAPI;
    }

    /* LobbyAPI. */
    public static LobbyAPI getLobbyAPI() {
        return lobbyAPI;
    }

    /* GameAPI. */
    public static GameAPI getGameAPI() {
        return gameAPI;
    }
}
