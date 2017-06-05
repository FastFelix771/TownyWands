/*******************************************************************************
 * Copyright (C) 2017 Felix Drescher-Hackel
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package de.fastfelix771.townywands.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Reflect {

	/**
	 * Placeholder for accessing static fields and methods
	 */
	public static final Object STATIC = null;
	
	/**
	 * Fetch the package version to build the correct class paths
	 * @return the package version part
	 */
	private static String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().substring(23) + ".";
	}

	/**
	 * Loads the requested class
	 * @param name the full path of the class
	 * @return the requested class or <code>null</code> if it doesn't exist or fails to load for any reason
	 */
	public static Class<?> getClass(String name) {
		try {
			return Class.forName(name);
		} catch(ClassNotFoundException e) {
			return null;
		}
	}

	@SneakyThrows(NoSuchMethodException.class)
	public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... params) {
		Constructor<?> constructor = clazz.getConstructor(params);
		constructor.setAccessible(true);
		
		return constructor;
	}

	@SneakyThrows(NoSuchMethodException.class)
	public static Method getMethod(Class<?> clazz, String name, Class<?>... params) {
		Method method = (params != null && !(params.length == 0)) ? clazz.getDeclaredMethod(name, params) : clazz.getDeclaredMethod(name);
		method.setAccessible(true);
		
		return method;
	}

	@SneakyThrows(NoSuchFieldException.class)
	public static Field getField(@NonNull Class<?> clazz, String name) {
		Field field = clazz.getDeclaredField(name);
		field.setAccessible(true);
		
		return field;
	}

	public static Class<?> getNMSClass(@NonNull String nmsName) {
		return getClass("net.minecraft.server.".concat(getVersion()).concat(nmsName));
	}

	public static Class<?> getCBClass(@NonNull String cbPackage, @NonNull String cbName) {
		return getClass("org.bukkit.craftbukkit.".concat(getVersion()).concat(cbPackage).concat(".").concat(cbName));
	}

}
