package de.deroq.nicksystem.game.implementations;

import de.deroq.nicksystem.api.GameAPI;
import de.deroq.nicksystem.api.NickSystem;
import de.deroq.nicksystem.api.models.NickList;
import de.deroq.nicksystem.api.models.NickUser;
import de.deroq.nicksystem.game.NickSystemGame;
import de.deroq.nicksystem.game.utils.Constants;
import de.deroq.nicksystem.game.utils.nms.NMSMethods;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author deroq
 * @since 10.07.2022
 */

public class GameAPIImplementation extends NMSMethods implements GameAPI {

    private final NickSystemGame nickSystemGame;

    public GameAPIImplementation(NickSystemGame nickSystemGame) {
        this.nickSystemGame = nickSystemGame;
    }

    @Override
    public void nickUser(NickUser nickUser, String name, String skin) {
        Player player = Bukkit.getPlayer(UUID.fromString(nickUser.getUuid()));
        setNickname(player, name);
        setSkin(player, skin);
        fakeRespawn(player);

        nickUser.setNickname(name);
        NickSystem.getNickAPI().updateNickUser(Collections.singletonList(nickUser));
    }

    @Override
    public void nickUser(NickUser nickUser) {
        NickList nickList = NickSystem.getNickAPI().getNickList().join();
        Random random = new Random();
        String nickname = nickList.getNicknames().get(random.nextInt(nickList.getNicknames().size()));
        String skin = nickList.getSkins().get(random.nextInt(nickList.getNicknames().size()));

        nickUser(nickUser, nickname, skin);
    }

    @Override
    public void unnickUser(NickUser nickUser, boolean fakeRespawn) {
        Player player = Bukkit.getPlayer(UUID.fromString(nickUser.getUuid()));

        setNickname(player, nickUser.getName());
        setSkin(player, nickUser.getName());

        if (fakeRespawn) {
            fakeRespawn(player);
        }

        nickUser.setNickname(null);
        NickSystem.getNickAPI().updateNickUser(Collections.singletonList(nickUser));
    }

    /**
     * Sets the skin of a player.
     *
     * @param player The player who gets the skin.
     * @param skin   The skin the player gets.
     */
    private void setSkin(Player player, String skin) {
        parseUUID(skin).thenAcceptAsync(uuid -> {
            if (uuid == null) {
                return;
            }

            parseTextures(uuid).thenAcceptAsync(textures -> {
                if (textures == null) {
                    return;
                }

                Bukkit.getScheduler().runTask(nickSystemGame, () -> setTextures(player, textures));
            });
        });
    }


    /**
     * Parses the uuid of a player by its name.
     *
     * @param name The name of the player whose uuid gets parsed.
     * @return a Future with a String.
     */
    private CompletableFuture<String> parseUUID(String name) {
        CompletableFuture<String> future = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            try {
                /* Open a new URLConnection to get the uuid by a name. */
                HttpURLConnection connection = (HttpURLConnection) new URL("https://api.mojang.com/users/profiles/minecraft/" + name).openConnection();
                connection.setRequestMethod("GET");

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;

                StringBuffer respone = new StringBuffer();
                while ((line = bufferedReader.readLine()) != null) {
                    respone.append(line);
                }

                bufferedReader.close();

                /* Get respone as JSONObject. */
                JSONObject jsonObject = new JSONObject(respone.toString());
                /* Get the uuid by getting the key id from the json. */
                String uuid = jsonObject.getString("id");
                future.complete(uuid);

            } catch (IOException e) {
                e.printStackTrace();
                future.complete(null);
            }
        }, Constants.EXECUTOR_SERVICE);

        return future;
    }

    /**
     * Parses the skin textures of a player by its uuid.
     *
     * @param uuid The uuid of the player whose textures gets parsed.
     * @return a Future with a StringArray.
     */
    private CompletableFuture<String[]> parseTextures(String uuid) {
        CompletableFuture<String[]> future = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            try {
                /* Open a new URLConnection to get the players texture by an uuid */
                HttpURLConnection connection = (HttpURLConnection) new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false").openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;

                StringBuffer respone = new StringBuffer();
                while ((line = reader.readLine()) != null) {
                    respone.append(line);
                }

                reader.close();

                /* Get respone as JSONObject. */
                JSONObject jsonObject = new JSONObject(respone.toString());
                /* Get properties array which contains the textures. */
                JSONArray properties = jsonObject.getJSONArray("properties");

                /* If i is lower than the array length, increment i. */
                for (int i = 0; i < properties.length(); i++) {
                    /* Get JSONObject of index i. */
                    JSONObject property = (JSONObject) properties.get(i);
                    future.complete(new String[]{property.getString("value"), property.getString("signature")});
                }
            } catch (IOException e) {
                e.printStackTrace();
                future.complete(null);
            }
        }, Constants.EXECUTOR_SERVICE);

        return future;
    }
}
