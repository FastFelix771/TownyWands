package me.fastfelix771.townywands.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Reflect {

	public static final Class<?> NBTTagCompound = getNMSClass("NBTTagCompound");
	public static final Class<?> CraftItemStack = getCBClass("inventory", "CraftItemStack");
	public static final Class<?> ItemStack = getNMSClass("ItemStack");

	public static Version getServerVersion() {
		return Version.fromString(getVersion());
	}

	public enum Version {
		UNKNOWN, v1_9, v1_8, v1_7, v1_6, v1_5, v1_4, v1_3, v1_2, v1_1;

		public static Version fromString(final String input) {
			final String tmp = input.replace("v", "");

			if (tmp.startsWith("1_1")) {
				return Version.v1_1;
			}

			if (tmp.startsWith("1_2")) {
				return Version.v1_2;
			}

			if (tmp.startsWith("1_3")) {
				return Version.v1_3;
			}

			if (tmp.startsWith("1_4")) {
				return Version.v1_4;
			}

			if (tmp.startsWith("1_5")) {
				return Version.v1_5;
			}

			if (tmp.startsWith("1_6")) {
				return Version.v1_6;
			}

			if (tmp.startsWith("1_7")) {
				return Version.v1_7;
			}

			if (tmp.startsWith("1_8")) {
				return Version.v1_8;
			}

			if (tmp.startsWith("1_9")) {
				return Version.v1_9;
			}

			return Version.UNKNOWN;
		}
	}

	// Coming Soon!
	public static Version getProtocolVersion(final Player player) {
		return Version.UNKNOWN;
	}

	public static Constructor<?> getConstructor(final Class<?> clazz, final Class<?>... params) {
		try {
			return clazz.getConstructor(params);
		} catch (final NoSuchMethodException e) {
			return null;
		}
	}

	public static boolean isCastable(final Object object, final Class<?> clazz) {
		try {
			clazz.cast(object);
			return true;
		} catch (final ClassCastException e) {
			return false;
		}
	}

	public static Object getNMSPlayer(final Player player) {
		try {
			final Method getHandle = player.getClass().getMethod("getHandle");
			final Object nms = getHandle.invoke(player);
			return nms;
		} catch (final Exception e) {
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
		} catch (final Exception e) {
			e.printStackTrace();
		}
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
		} catch (final ClassNotFoundException e) {
			return null;
		}
	}

	public static Class<?> getCBClass(final String cbPackage, final String cbName) {
		final String name = "org.bukkit.craftbukkit." + getVersion() + cbPackage + "." + cbName;
		try {
			return Class.forName(name);
		} catch (final ClassNotFoundException e) {
			return null;
		}
	}

}