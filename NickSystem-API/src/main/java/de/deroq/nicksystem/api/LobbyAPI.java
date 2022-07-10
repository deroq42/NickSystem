package de.deroq.nicksystem.api;

import de.deroq.nicksystem.api.models.NickUser;

public interface LobbyAPI {

    /**
     * Enables or disables the automatic nickname.
     *
     * @param nickUser The NickUser who sets automatic nickname.
     * @param autoNick The automatic nickname setting.
     */
    void setAutoNick(NickUser nickUser, boolean autoNick);
}
