package de.deroq.nicksystem.api;

import de.deroq.nicksystem.api.models.NickUser;

public interface GameAPI {

    /**
     * Sets a users nickname.
     *
     * @param nickUser The NickUser who gets a nickname.
     * @param name The nickname.
     * @param skin The skin of the nickname.
     */
    void nickUser(NickUser nickUser, String name, String skin);

    /**
     * Set the user a random nickname.
     *
     * @param nickUser The NickUser who gets a nickname.
     */
    void nickUser(NickUser nickUser);

    /**
     * Removes a users nickname.
     *
     * @param nickUser The NickUser whose nickname gets removed.
     * @param fakeRespawn If true, the player gets respawned.
     */
    void unnickUser(NickUser nickUser, boolean fakeRespawn);
}
