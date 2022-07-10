package de.deroq.nicksystem.lobby.implementations;

import de.deroq.nicksystem.api.LobbyAPI;
import de.deroq.nicksystem.api.NickSystem;
import de.deroq.nicksystem.api.models.NickUser;

import java.util.Collections;

public class LobbyAPIImplementation implements LobbyAPI {

    @Override
    public void setAutoNick(NickUser nickUser, boolean autoNick) {
        nickUser.setAutoNick(autoNick);
        NickSystem.getNickAPI().updateNickUser(Collections.singletonList(nickUser));
    }
}
