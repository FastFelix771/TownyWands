package de.fastfelix771.townywands.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.NonNull;
import lombok.SneakyThrows;

public class Reflect {

	public static final Class<?> NBTTagCompound = getNMSClass("NBTTagCompound");
	public static final Class<?> NBTBase = getNMSClass("NBTBase");
	public static final Class<?> CraftItemStack = getCBClass("inventory", "CraftItemStack");
	public static final Class<?> ItemStack = getNMSClass("ItemStack");

	// PACKETS //
	public static final Class<?> PacketPlayInUpdateSign = getNMSClass("PacketPlayInUpdateSign");

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