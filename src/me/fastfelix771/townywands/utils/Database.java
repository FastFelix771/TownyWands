package me.fastfelix771.townywands.utils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import me.fastfelix771.townywands.inventory.ModularGUI;
import me.fastfelix771.townywands.lang.Language;

import org.bukkit.inventory.Inventory;

public class Database {

	private static final ConcurrentHashMap<String, ModularGUI> storage = new ConcurrentHashMap<String, ModularGUI>();

	public static List<ModularGUI> guiList() {
		return Arrays.asList(storage.values().toArray(new ModularGUI[storage.values().size()]));
	}

	public static Inventory get(final String command, final Language language) {
		if (!contains(command)) {
			return null;
		}

		return storage.get(command).get(language);
	}

	public static ModularGUI get(final String command) {
		if (!contains(command)) {
			return null;
		}

		return storage.get(command);
	}

	public static void add(final String command, final ModularGUI gui) {
		storage.putIfAbsent(command, gui);
	}

	public static boolean contains(final String command) {
		return storage.containsKey(command);
	}

	public static boolean contains(final String command, final Language language) {
		if (!contains(command)) {
			return false;
		}
		return get(command).get(language) != null;
	}

	public static void remove(final String command) {
		storage.remove(command);
	}

	public static void clear() {
		storage.clear();
	}

}