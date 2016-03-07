package de.fastfelix771.townywands.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Reflect {

    public static final Class<?> NBTTagCompound = getNMSClass("NBTTagCompound");
    public static final Class<?> NBTBase = getNMSClass("NBTBase");
    public static final Class<?> CraftItemStack = getCBClass("inventory", "CraftItemStack");
    public static final Class<?> ItemStack = getNMSClass("ItemStack");
    @Getter(
        lazy = true)
    private static final Version serverVersion = Version.fromString(getVersion());

    public enum Version {
        UNKNOWN, v1_10, v1_9, v1_8, v1_7, v1_6, v1_5, v1_4, v1_3, v1_2, v1_1, v1_0;

        public static Version fromString(final String input) {
            final String tmp = input.replace("v", "");

            if (tmp.startsWith("1_1")) return Version.v1_1;

            if (tmp.startsWith("1_2")) return Version.v1_2;

            if (tmp.startsWith("1_3")) return Version.v1_3;

            if (tmp.startsWith("1_4")) return Version.v1_4;

            if (tmp.startsWith("1_5")) return Version.v1_5;

            if (tmp.startsWith("1_6")) return Version.v1_6;

            if (tmp.startsWith("1_7")) return Version.v1_7;

            if (tmp.startsWith("1_8")) return Version.v1_8;

            if (tmp.startsWith("1_9")) return Version.v1_9;

            return Version.UNKNOWN;
        }
    }

    public static Class<?> getClass(@NonNull String clazz) {
        try {
            return Class.forName(clazz);
        }
        catch (final ClassNotFoundException e) {
            return null;
        }
    }

    // Coming Soon!
    public static Version getProtocolVersion(final Player player) {
        return Version.UNKNOWN;
    }

    public static Constructor<?> getConstructor(final Class<?> clazz, final Class<?>... params) {
        try {
            final Constructor<?> constructor = clazz.getConstructor(params);
            constructor.setAccessible(true);
            return constructor;
        }
        catch (final NoSuchMethodException e) {
            System.out.println("");
            return null;
        }
    }

    public static Object getNMSPlayer(final Player player) {
        try {
            final Method getHandle = player.getClass().getMethod("getHandle");
            final Object nms = getHandle.invoke(player);
            return nms;
        }
        catch (final Exception e) {
            return null;
        }
    }

    public static Method getMethod(final Method method) {
        method.setAccessible(true);
        return method;
    }

    public static void sendPacket(final Player player, final Object packet) {
        try {
            final Method getHandle = player.getClass().getMethod("getHandle");
            final Object nmsPlayer = getHandle.invoke(player);
            final Field pConnectionField = nmsPlayer.getClass().getField("playerConnection");
            final Object pConnection = pConnectionField.get(nmsPlayer);
            final Method sendMethod = pConnection.getClass().getMethod("sendPacket", new Class[] { getNMSClass("Packet") });
            sendMethod.invoke(pConnection, new Object[] { packet });
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public static Object getNMSItem(@NonNull ItemStack item) {
        try {
            final Method asNMSCopy = getMethod(CraftItemStack.getDeclaredMethod("asNMSCopy", ItemStack.class));
            final Object nms = asNMSCopy.invoke(null, item); // null ist testweise, war mal CraftItemStack
            return nms;
        }
        catch (Exception e) {
            return null;
        }
    }

    @SneakyThrows
    public static ItemStack getBukkitItem(@NonNull Object nms) {
        Method asCraftMirror = Reflect.getMethod(Reflect.CraftItemStack.getDeclaredMethod("asCraftMirror", Reflect.ItemStack));
        ItemStack itemStack = (ItemStack) asCraftMirror.invoke(null, nms);
        return itemStack;
    }

    public static Field getField(final Field field) {
        field.setAccessible(true);
        return field;
    }

    public static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().substring(23) + ".";
    }

    public static Class<?> getNMSClass(final String nmsName) {
        final String name = "net.minecraft.server." + getVersion() + nmsName;
        try {
            return Class.forName(name);
        }
        catch (final ClassNotFoundException e) {
            return null;
        }
    }

    public static Class<?> getCBClass(final String cbPackage, final String cbName) {
        final String name = "org.bukkit.craftbukkit." + getVersion() + cbPackage + "." + cbName;
        try {
            return Class.forName(name);
        }
        catch (final ClassNotFoundException e) {
            return null;
        }
    }

}