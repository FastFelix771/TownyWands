package de.fastfelix771.townywands.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Reflect {

	@Getter
	private static Reflect instance = new Reflect();

	public static final Class<?> CraftItemStack = getInstance().getCBClass("inventory", "CraftItemStack");
	public static final Class<?> ItemStack = getInstance().getNMSClass("ItemStack");

	private static String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().substring(23) + ".";
	}

	public Class<?> getClass(@NonNull String name) {
		try {
			return Class.forName(name);
		} catch(ClassNotFoundException e) {
			return null;
		}
	}

	@SneakyThrows(NoSuchMethodException.class)
	public Constructor<?> getConstructor(Class<?> clazz, Class<?>... params) {
		Constructor<?> constructor = clazz.getConstructor(params);
		constructor.setAccessible(true);
		return constructor;
	}

	@SneakyThrows(NoSuchMethodException.class)
	public Method getMethod(Class<?> clazz, String name, Class<?>... params) {
		Method method = (params != null && !(params.length == 0)) ? clazz.getDeclaredMethod(name, params) : clazz.getDeclaredMethod(name);
		method.setAccessible(true);
		return method;
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