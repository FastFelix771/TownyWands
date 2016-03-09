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
    public static final Class<?> PacketPlayInUpdateSign = getNMSClass("PacketPlayInUpdateSign");
    
    @Getter(lazy = true) private static final Version serverVersion = Version.fromString(getVersion());

    public enum Version {
        UNKNOWN, v1_10, v1_9, v1_8, v1_7, v1_6, v1_5, v1_4, v1_3, v1_2, v1_1, v1_0;

        public static Version fromString(final String input) {
            final String tmp = input.replace("v", "");

            if (tmp.startsWith("1_0")) return v1_0;

            if (tmp.startsWith("1_1")) return v1_1;

            if (tmp.startsWith("1_2")) return v1_2;

            if (tmp.startsWith("1_3")) return v1_3;

            if (tmp.startsWith("1_4")) return v1_4;

            if (tmp.startsWith("1_5")) return v1_5;

            if (tmp.startsWith("1_6")) return v1_6;

            if (tmp.startsWith("1_7")) return v1_7;

            if (tmp.startsWith("1_8")) return v1_8;

            if (tmp.startsWith("1_9")) return v1_9;

            if(tmp.startsWith("1_10")) return v1_10;

            return Version.UNKNOWN;
        }
    }

    public static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().substring(23) + ".";
    }

    public static Class<?> getClass(@NonNull String clazz) {
        try {
            return Class.forName(clazz);
        }
        catch (final ClassNotFoundException e) {
            return null;
        }
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

    public static Method getMethod(Method method) {
        method.setAccessible(true);
        return method;
    }

    public static Object getNMSItem(@NonNull ItemStack item) {
        try {
            final Method asNMSCopy = getMethod(CraftItemStack.getDeclaredMethod("asNMSCopy", ItemStack.class));
            final Object nms = asNMSCopy.invoke(null, item);
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

    public static Class<?> getNMSClass(String nmsName) {
        try {
            return Class.forName("net.minecraft.server.".concat(getVersion()).concat(nmsName));
        }
        catch (final ClassNotFoundException e) {
            return null;
        }
    }

    public static Class<?> getCBClass(String cbPackage, String cbName) {
        try {
            return Class.forName("org.bukkit.craftbukkit.".concat(getVersion()).concat(cbPackage).concat(".").concat(cbName));
        }
        catch (final ClassNotFoundException e) {
            return null;
        }
    }

}