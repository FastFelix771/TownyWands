package me.fastfelix771.townywands.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;

public class Reflect {

	public static final Class<?> NBTTagCompound = getNMSClass("NBTTagCompound");
	public static final Class<?> CraftItemStack = getCBClass("inventory", "CraftItemStack");
	public static final Class<?> ItemStack = getNMSClass("ItemStack");

	public static Constructor<?> getConstructor(final Class<?> clazz, final Class<?>... params) {
		try {
			return clazz.getConstructor(params);
		} catch (final NoSuchMethodException e) {
			return null;
		}
	}

	public static Method getMethod(final Method method) {
		method.setAccessible(true);
		return method;
	}

	public static Field getField(final Field field) {
		field.setAccessible(true);
		return field;
	}

	public static String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
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