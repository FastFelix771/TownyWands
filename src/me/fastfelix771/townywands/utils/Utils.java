package me.fastfelix771.townywands.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.fastfelix771.townywands.lang.Language;

import org.bukkit.inventory.ItemStack;

import com.google.gson.Gson;

public class Utils {

	private static final Integer[] validCounts = new Integer[] { 9, 18, 27, 36, 45, 54 };

	/*
	 * Maybe ill need this snippet sometime...
	 * public static Resident getResident(final Player player) {
	 * final String name = player.getName();
	 * Resident res;
	 * try {
	 * res = TownyUniverse.getDataSource().getResident(name);
	 * } catch (final NotRegisteredException e) {
	 * res = new Resident(name);
	 * TownyUniverse.getDataSource().saveResident(res);
	 * }
	 * return res;
	 * }
	 */

	public static boolean isValidSlotCount(final int slots) {
		return Arrays.asList(validCounts).contains(slots);
	}

	public static ItemStack setCommands(ItemStack item, final List<String> commands, final Language language) {
		try {
			final Method asNMSCopy = Reflect.getMethod(Reflect.CraftItemStack.getDeclaredMethod("asNMSCopy", ItemStack.class));
			final Object nms = asNMSCopy.invoke(Reflect.CraftItemStack, item);
			final Constructor<?> NBTTagCompound = Reflect.getConstructor(Reflect.NBTTagCompound);
			final Method hasTag = Reflect.getMethod(Reflect.ItemStack.getDeclaredMethod("hasTag"));
			final Method getTag = Reflect.getMethod(Reflect.ItemStack.getDeclaredMethod("getTag"));
			final Method setTag = Reflect.getMethod(Reflect.ItemStack.getDeclaredMethod("setTag", Reflect.NBTTagCompound));
			final Object tag = ((boolean) hasTag.invoke(nms) ? getTag.invoke(nms) : NBTTagCompound.newInstance());
			final Method setString = Reflect.getMethod(Reflect.NBTTagCompound.getDeclaredMethod("setString", String.class, String.class));
			final Method asCraftMirror = Reflect.getMethod(Reflect.CraftItemStack.getDeclaredMethod("asCraftMirror", Reflect.ItemStack));

			final Gson gson = new Gson();
			final String json = gson.toJson(commands);

			setString.invoke(tag, "townywands_commands_" + language.getCode(), json);

			if (!(boolean) hasTag.invoke(nms)) {
				setTag.invoke(nms, tag);
			}

			item = (ItemStack) asCraftMirror.invoke(null, nms);
			return item;
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static List<String> getCommands(final ItemStack item, final Language language) {
		try {
			final Method asNMSCopy = Reflect.getMethod(Reflect.CraftItemStack.getDeclaredMethod("asNMSCopy", ItemStack.class));
			final Object nms = asNMSCopy.invoke(Reflect.CraftItemStack, item);
			final Constructor<?> NBTTagCompound = Reflect.getConstructor(Reflect.NBTTagCompound);
			final Method hasTag = Reflect.getMethod(Reflect.ItemStack.getDeclaredMethod("hasTag"));
			final Method getTag = Reflect.getMethod(Reflect.ItemStack.getDeclaredMethod("getTag"));
			final Object tag = ((boolean) hasTag.invoke(nms) ? getTag.invoke(nms) : NBTTagCompound.newInstance());
			final Method getString = Reflect.getMethod(Reflect.NBTTagCompound.getDeclaredMethod("getString", String.class));

			if (getString.invoke(tag, "townywands_commands_" + language.getCode()) != null) {
				final Gson gson = new Gson();
				final List<String> commands = gson.fromJson((String) getString.invoke(tag, "townywands_commands_" + language.getCode()), ArrayList.class);
				return commands;
			}
			return null;
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static ItemStack setKey(ItemStack item, final String key) {
		try {
			final Method asNMSCopy = Reflect.getMethod(Reflect.CraftItemStack.getDeclaredMethod("asNMSCopy", ItemStack.class));
			final Object nms = asNMSCopy.invoke(Reflect.CraftItemStack, item);
			final Constructor<?> NBTTagCompound = Reflect.getConstructor(Reflect.NBTTagCompound);
			final Method hasTag = Reflect.getMethod(Reflect.ItemStack.getDeclaredMethod("hasTag"));
			final Method getTag = Reflect.getMethod(Reflect.ItemStack.getDeclaredMethod("getTag"));
			final Method setTag = Reflect.getMethod(Reflect.ItemStack.getDeclaredMethod("setTag", Reflect.NBTTagCompound));
			final Object tag = ((boolean) hasTag.invoke(nms) ? getTag.invoke(nms) : NBTTagCompound.newInstance());
			final Method setString = Reflect.getMethod(Reflect.NBTTagCompound.getDeclaredMethod("setString", String.class, String.class));
			final Method asCraftMirror = Reflect.getMethod(Reflect.CraftItemStack.getDeclaredMethod("asCraftMirror", Reflect.ItemStack));

			setString.invoke(tag, "townywands_key", key);

			if (!(boolean) hasTag.invoke(nms)) {
				setTag.invoke(nms, tag);
			}

			item = (ItemStack) asCraftMirror.invoke(null, nms);
			return item;
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getKey(final ItemStack item) {
		try {
			final Method asNMSCopy = Reflect.getMethod(Reflect.CraftItemStack.getDeclaredMethod("asNMSCopy", ItemStack.class));
			final Object nms = asNMSCopy.invoke(Reflect.CraftItemStack, item);
			final Constructor<?> NBTTagCompound = Reflect.getConstructor(Reflect.NBTTagCompound);
			final Method hasTag = Reflect.getMethod(Reflect.ItemStack.getDeclaredMethod("hasTag"));
			final Method getTag = Reflect.getMethod(Reflect.ItemStack.getDeclaredMethod("getTag"));
			final Object tag = ((boolean) hasTag.invoke(nms) ? getTag.invoke(nms) : NBTTagCompound.newInstance());
			final Method getString = Reflect.getMethod(Reflect.NBTTagCompound.getDeclaredMethod("getString", String.class));

			final String key = (String) getString.invoke(tag, "townywands_key");

			// If the item has no key it will return an empty string...that would block 90% of all InventoryClickEvents, so check for it and return null instead.
			if (key.isEmpty() || key == "" || key == null) {
				return null;
			}

			return key;
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static ItemStack addEnchantmentGlow(ItemStack item) {
		try {
			final Method asNMSCopy = Reflect.getMethod(Reflect.CraftItemStack.getDeclaredMethod("asNMSCopy", ItemStack.class));
			final Object nms = asNMSCopy.invoke(Reflect.CraftItemStack, item);
			final Constructor<?> NBTTagCompound = Reflect.getConstructor(Reflect.NBTTagCompound);
			final Method hasTag = Reflect.getMethod(Reflect.ItemStack.getDeclaredMethod("hasTag"));
			final Method getTag = Reflect.getMethod(Reflect.ItemStack.getDeclaredMethod("getTag"));
			final Method setTag = Reflect.getMethod(Reflect.ItemStack.getDeclaredMethod("setTag", Reflect.NBTTagCompound));
			final Object tag = ((boolean) hasTag.invoke(nms) ? getTag.invoke(nms) : NBTTagCompound.newInstance());
			final Method setString = Reflect.getMethod(Reflect.NBTTagCompound.getDeclaredMethod("setString", String.class, String.class));
			final Method asCraftMirror = Reflect.getMethod(Reflect.CraftItemStack.getDeclaredMethod("asCraftMirror", Reflect.ItemStack));

			final String enchantments = Reflect.getConstructor(Reflect.getNMSClass("NBTTagList")).newInstance().toString();
			setString.invoke(tag, "ench", enchantments);

			if (!(boolean) hasTag.invoke(nms)) {
				setTag.invoke(nms, tag);
			}

			item = (ItemStack) asCraftMirror.invoke(null, nms);
			return item;
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}