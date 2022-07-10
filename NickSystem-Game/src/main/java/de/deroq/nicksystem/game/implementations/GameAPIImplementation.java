package de.deroq.nicksystem.game.implementations;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.deroq.nicksystem.api.GameAPI;
import de.deroq.nicksystem.api.NickSystem;
import de.deroq.nicksystem.api.models.NickList;
import de.deroq.nicksystem.api.models.NickUser;
import de.deroq.nicksystem.game.NickSystemGame;
import de.deroq.nicksystem.game.utils.Constants;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class GameAPIImplementation implements GameAPI {

    private final NickSystemGame nickSystemGame;

    public GameAPIImplementation(NickSystemGame nickSystemGame) {
        this.nickSystemGame = nickSystemGame;
    }

    /*
     * SOON: REFLECTIONS
     */


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
     * Sets the nickname of a player.
     *
     * @param player   The player who gets the nickname.
     * @param nickname The nickname the player gets.
     */
    private void setNickname(Player player, String nickname) {
        CraftPlayer craftPlayer = (CraftPlayer) player;

        try {
            /* Set the name inside the field of the players GameProfile. */
            Objects.requireNonNull(getField(GameProfile.class, "name")).set(craftPlayer.getProfile(), nickname);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
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

                Bukkit.getScheduler().runTask(nickSystemGame, () -> {
                    GameProfile gameProfile = ((CraftPlayer) player).getProfile();
                    gameProfile.getProperties().removeAll("textures");
                    /* Put the new textures into the properties. */
                    gameProfile.getProperties().put("textures", new Property("textures", textures[0], textures[1]));
                });
            });
        });
    }

    /**
     * Performs a fake respawn.
     *
     * @param player The Player who gets respawned.
     */
    private void fakeRespawn(Player player) {
        CraftPlayer craftPlayer = ((CraftPlayer) player);

        for (Player players : Bukkit.getOnlinePlayers()) {
            /* Real Skin and name will be visible for himself and operators. */
            if (players.equals(player) || players.isOp()) {
                continue;
            }

            /* Destroy Player and remove him from the tablist. */
            sendPacket(players, new PacketPlayOutEntityDestroy(craftPlayer.getEntityId()));
            sendPacket(players, new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, craftPlayer.getHandle()));

            Bukkit.getScheduler().runTaskLater(nickSystemGame, () -> {
                /* Add player to the tablist and spawn him with new skin and name. */
                sendPacket(players, new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, craftPlayer.getHandle()));
                sendPacket(players, new PacketPlayOutNamedEntitySpawn(craftPlayer.getHandle()));
            }, 10);
        }
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

    private void sendPacket(Packet<?> packet) {
        Bukkit.getOnlinePlayers().forEach(player -> ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet));
    }

    private void sendPacket(Player player, Packet<?> packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    private Field getField(Class<?> clazz, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }
}
