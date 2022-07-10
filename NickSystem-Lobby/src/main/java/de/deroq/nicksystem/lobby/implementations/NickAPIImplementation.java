package de.deroq.nicksystem.lobby.implementations;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import de.deroq.database.services.mongo.MongoDatabaseServiceMethods;
import de.deroq.nicksystem.api.NickAPI;
import de.deroq.nicksystem.api.models.NickList;
import de.deroq.nicksystem.api.models.NickUser;
import de.deroq.nicksystem.lobby.NickSystemLobby;
import de.deroq.nicksystem.lobby.implementations.models.NickListImplementation;
import de.deroq.nicksystem.lobby.implementations.models.NickUserImplementation;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class NickAPIImplementation implements NickAPI {

    private final MongoDatabaseServiceMethods databaseServiceMethods;
    private final MongoCollection<NickUserImplementation> nickUsersCollection;
    private final MongoCollection<NickListImplementation> nickListCollection;

    public NickAPIImplementation(NickSystemLobby nickSystemLobby) {
        this.databaseServiceMethods = nickSystemLobby.getDatabaseService().getDatabaseServiceMethods();
        this.nickUsersCollection = nickSystemLobby.getDatabaseService().getCollection("nickUsers", NickUserImplementation.class);
        this.nickListCollection = nickSystemLobby.getDatabaseService().getCollection("nickList", NickListImplementation.class);

        createNickList();
    }

    @Override
    public CompletableFuture<Boolean> createNickUser(UUID uuid, String name) {
        NickUserImplementation nickUser = new NickUserImplementation.builder()
                .setUuid(uuid.toString())
                .setName(name)
                .setAutoNick(false)
                .setNickname(null)
                .build();

        return databaseServiceMethods.onInsert(
                nickUsersCollection,
                Filters.eq("uuid", uuid.toString()),
                nickUser);
    }

    @Override
    public CompletableFuture<Boolean> updateNickUser(List<? extends NickUser> nickUser) {
        NickUserImplementation nickUserImplementation = (NickUserImplementation) nickUser.get(0);

        return databaseServiceMethods.onUpdate(
                nickUsersCollection,
                Filters.eq("uuid", nickUserImplementation.getUuid()),
                nickUserImplementation);
    }

    @Override
    public CompletableFuture<NickUserImplementation> getNickUser(UUID uuid) {
        return databaseServiceMethods.getAsync(
                nickUsersCollection,
                Filters.eq("uuid", uuid.toString()));
    }

    @Override
    public CompletableFuture<NickUserImplementation> getNickUser(String name) {
        return databaseServiceMethods.getAsync(
                nickUsersCollection,
                Filters.eq("name", name));
    }

    @Override
    public CompletableFuture<Boolean> createNickList() {
        NickListImplementation nickList = NickListImplementation.create(
                Collections.singletonList("GommeHD"),
                Collections.singletonList("GommeHD"));

        return databaseServiceMethods.onInsert(
                nickListCollection,
                Filters.exists("nicknames"),
                nickList);
    }

    @Override
    public CompletableFuture<Boolean> updateNickList(List<? extends NickList> nickList) {
        NickListImplementation nickListImplementation = (NickListImplementation) nickList.get(0);

        return databaseServiceMethods.onUpdate(
                nickListCollection,
                Filters.exists("nicknames"),
                nickListImplementation);
    }

    @Override
    public CompletableFuture<? extends NickList> getNickList() {
        return databaseServiceMethods.getAsync(
                nickListCollection,
                Filters.exists("nicknames"));
    }
}
