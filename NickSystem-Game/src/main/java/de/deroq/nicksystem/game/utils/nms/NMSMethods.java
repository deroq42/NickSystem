package de.deroq.nicksystem.game.utils.nms;

import de.deroq.nicksystem.game.NickSystemGame;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author deroq
 * @since 10.07.2022
 */

public class NMSMethods extends Reflections {

    /**
     * Sets the nickname of a player.
     *
     * @param player   The player who gets the nickname.
     * @param nickname The nickname the player gets.
     */
    public void setNickname(Player player, String nickname) {
        try {
            Object handle = getCraftBukkitClass("entity.CraftPlayer").getMethod("getHandle").invoke(player);
            Object profile = handle.getClass().getMethod("getProfile").invoke(handle);

            /* Set the name inside the field of the players GameProfile. */
            setValue(profile, "name", nickname);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the skin textures for a player.
     *
     * @param player   The player to set the textures.
     * @param textures The textures to set.
     */
    public void setTextures(Player player, String[] textures) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object profile = handle.getClass().getMethod("getProfile").invoke(handle);
            Object propertyMap = profile.getClass().getMethod("getProperties").invoke(profile);

            /* Call the removeAll method, so we can remove the old textures from the properties. */
            propertyMap.getClass().getMethod("removeAll", Object.class).invoke(propertyMap, "textures");

            /* Gets the Property class and creates a new instance of it with params String, String, String. */
            Class<?> propertyClass = Class.forName("com.mojang.authlib.properties.Property");
            Constructor<?> propertyConstructor = propertyClass.getConstructor(String.class, String.class, String.class);
            Object property = propertyConstructor.newInstance("textures", textures[0], textures[1]);

            /* Call the put method, so we can put the new textures into the properties. */
            propertyMap.getClass().getMethod("put", Object.class, Object.class).invoke(propertyMap, "textures", property);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | ClassNotFoundException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Performs a fake respawn.
     *
     * @param player The Player who gets respawned.
     */
    public void fakeRespawn(Player player) {
        for (Player players : Bukkit.getOnlinePlayers()) {
            if (players.equals(player) /*|| players.isOp()*/) {
                continue;
            }

            destroy(player, players);
            setTablist(player, players, "REMOVE_PLAYER");

            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(NickSystemGame.class), () -> {
                setTablist(player, players, "ADD_PLAYER");
                spawn(player, players);
            }, 10);
        }
    }

    /**
     * Spawns the player.
     *
     * @param player The player to spawn.
     */
    private void spawn(Player player, Player packetReceiver) {
        try {
            /* Gets the PacketPlayOutNamedEntitySpawn class and creates a new instance of it with param EntityPlayer. */
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Class<?> packetPlayOutNamedEntitySpawnClass = getNMSClass("PacketPlayOutNamedEntitySpawn");
            Constructor<?> packetPlayOutNamedEntitySpawnConstructor = packetPlayOutNamedEntitySpawnClass.getConstructor(getNMSClass("EntityHuman"));
            Object packetPlayOutNamedEntitySpawn = packetPlayOutNamedEntitySpawnConstructor.newInstance(handle);
            sendPacket(packetReceiver, packetPlayOutNamedEntitySpawn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void destroy(Player player, Player packetReceiver) {
        try {
            /* Gets the entity id. */
            Object id = player.getClass().getMethod("getEntityId").invoke(player);

            /* Gets the PacketPlayOutEntityDestroy class and creates a new instance of it with param int[]. */
            Class<?> packetPlayOutEntityDestroyClass = getNMSClass("PacketPlayOutEntityDestroy");
            Constructor<?> packetPlayOutEntityDestroyConstructor = packetPlayOutEntityDestroyClass.getConstructor(int[].class);
            Object packetPlayOutEntityDestroy = packetPlayOutEntityDestroyConstructor.newInstance(new int[]{(int) id});
            sendPacket(packetReceiver, packetPlayOutEntityDestroy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds or remove the player from the tablist.
     *
     * @param player The player to add or remove.
     * @param action The action add to the tablist or remove from the tablist.
     */
    private void setTablist(Player player, Player packetReceiver, String action) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object array = Array.newInstance(getNMSClass("EntityPlayer"), 1);
            Array.set(array, 0, handle);

            /* Gets the PacketPlayOutPlayerInfo class and the inner class EnumPlayerInfoAction to add/remove the player to the tablist */
            Class<?> packetPlayOutPlayerInfoClass = getNMSClass("PacketPlayOutPlayerInfo");
            Class<?> enumPlayerInfoActionClass = getNMSClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction");
            Object addPlayerEnum = enumPlayerInfoActionClass
                    .getField(action)
                    .get(null);

            Constructor<?> packetPlayOutPlayerInfoConstructor = packetPlayOutPlayerInfoClass.getConstructor(
                    enumPlayerInfoActionClass,
                    Class.forName("[Lnet.minecraft.server." + getServerVersion() + ".EntityPlayer;"));

            /* Creates a new instance of it with the params EnumPlayerInfoAction, EntityPlayer. */
            Object packetPlayOutPlayerInfo = packetPlayOutPlayerInfoConstructor.newInstance(addPlayerEnum, array);
            sendPacket(packetReceiver, packetPlayOutPlayerInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
