package de.deroq.nicksystem.game.utils.nms;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * @author deroq
 * @since 10.07.2022
 */

public class Reflections {

    public void sendPacket(Object packet) {
        for (Player players : Bukkit.getOnlinePlayers()) {
            try {
                Object handle = players.getClass().getMethod("getHandle").invoke(players);
                Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
                playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
            } catch (NoSuchFieldException | InvocationTargetException | IllegalAccessException |
                     NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (NoSuchFieldException | InvocationTargetException | IllegalAccessException |
                 NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    public Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + getServerVersion() + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Class<?> getCraftBukkitClass(String name) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + getServerVersion() + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getServerVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    public void setValue(Object object, String name, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object getValue(Object object, String name) {
        try {
            Field field = object.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Field getField(Class<?> clazz, String name) {
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
