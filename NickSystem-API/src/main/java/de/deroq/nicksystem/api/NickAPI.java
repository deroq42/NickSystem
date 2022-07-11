package de.deroq.nicksystem.api;

import de.deroq.nicksystem.api.models.NickList;
import de.deroq.nicksystem.api.models.NickUser;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author deroq
 * @since 10.07.2022
 */

public interface NickAPI {

    /**
     * Creates a NickUser.
     *
     * @param uuid The uuid of the user.
     * @param name The name of the user.
     * @return a Future with a Boolean which returns false if the NickUser has been inserted.
     */
    CompletableFuture<Boolean> createNickUser(UUID uuid, String name);

    /**
     * Updates a NickUser.
     *
     * @param nickUser A singleton list of the NickUser.
     * @return a Future with a Boolean which returns false if the NickUser has been updated.
     */
    CompletableFuture<Boolean> updateNickUser(List<? extends NickUser> nickUser);

    /**
     * Gets a NickUser by his uuid.
     *
     * @param uuid The uuid of the user.
     * @return a Future with a NickUser.
     */
    CompletableFuture<? extends NickUser> getNickUser(UUID uuid);

    /**
     * Gets a NickUser by his name.
     *
     * @param name The name of the user.
     * @return a Future with a NickUser.
     */
    CompletableFuture<? extends NickUser> getNickUser(String name);

    /**
     * Creates the NickList.
     *
     * @return a Future with a Boolean which returns false if the NickList has been inserted.
     */
    CompletableFuture<Boolean> createNickList();

    /**
     * Updates the NickList.
     *
     * @param nickList A singleton list of the NickList.
     * @return a Future with a Boolean which returns false if the NickList has been updated.
     */
    CompletableFuture<Boolean> updateNickList(List<? extends NickList> nickList);

    /**
     * Gets the NickList.
     *
     * @return a Future with the NickList.
     */
    CompletableFuture<? extends NickList> getNickList();
}
