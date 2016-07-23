package de.fastfelix771.townywands.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Reflect {

	@Getter
	private static Reflect instance = new Reflect();

	public static final Class<?> NBTTagCompound = getInstance().getNMSClass("NBTTagCompound");
	public static final Class<?> NBTBase = getInstance().getNMSClass("NBTBase");
	public static final Class<?> CraftItemStack = getInstance().getCBClass("inventory", "CraftItemStack");
	public static final Class<?> ItemStack = getInstance().getNMSClass("ItemStack");

	private static String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().substring(23) + ".";
	}

	@SneakyThrows(ClassNotFoundException.class)
	public Class<?> getClass(@NonNull String clazz) {
		return Class.forName(clazz);
	}

	@SneakyThrows(NoSuchMethodException.class)
	public Constructor<?> getConstructor(Class<?> clazz, Class<?>... params) {
		Constructor<?> constructor = clazz.getConstructor(params);
		constructor.setAccessible(true);
		return constructor;
	}

	@SneakyThrows(value = { InvocationTargetException.class, IllegalAccessException.class })
	public Object getNMSPlayer(Player player) {
		return getMethod(player.getClass(), "getHandle").invoke(player);
	}

	@SneakyThrows(NoSuchMethodException.class)
	public Method getMethod(Class<?> clazz, String name, Class<?>... params) {
		Method method = (params != null && !(params.length == 0)) ? clazz.getDeclaredMethod(name, params) : clazz.getDeclaredMethod(name);
		method.setAccessible(true);
		return method;
	}

	@SneakyThrows(value = { InvocationTargetException.class, IllegalAccessException.class })
	public Object getNMSItem(@NonNull ItemStack item) {
		return getMethod(CraftItemStack, "asNMSCopy", ItemStack.class).invoke(null, item);
	}

	@SneakyThrows(value = { InvocationTargetException.class, IllegalAccessException.class })
	public ItemStack getBukkitItem(@NonNull Object nms) {
		return (ItemStack) getMethod(CraftItemStack, "asCraftMirror", ItemStack).invoke(null, nms);
	}

	@SneakyThrows(NoSuchFieldException.class)
	public Field getField(@NonNull Class<?> clazz, String name) {
		Field field = clazz.getDeclaredField(name);
		field.setAccessible(true);
		return field;
	}

	public Class<?> getNMSClass(@NonNull String nmsName) {
		return getClass("net.minecraft.server.".concat(getVersion()).concat(nmsName));
	}

	public Class<?> getCBClass(@NonNull String cbPackage, @NonNull String cbName) {
		return getClass("org.bukkit.craftbukkit.".concat(getVersion()).concat(cbPackage).concat(".").concat(cbName));
	}

}