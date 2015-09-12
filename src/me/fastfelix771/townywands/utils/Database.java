package me.fastfelix771.townywands.utils;

import java.util.concurrent.ConcurrentHashMap;

import me.fastfelix771.townywands.lang.Language;

public class Database {

	private static final ConcurrentHashMap<String, ConcurrentHashMap<Language, DataBundle>> storage = new ConcurrentHashMap<String, ConcurrentHashMap<Language, DataBundle>>();

	public static DataBundle get(final String command, final Language language) {
		if (!contains(command)) {
			return null;
		}

		return storage.get(command).get(language);
	}

	public static void add(final String command, final DataBundle db) {
		if (!contains(command)) {
			final ConcurrentHashMap<Language, DataBundle> map = new ConcurrentHashMap<Language, DataBundle>();
			map.put(db.getLanguage(), db);
			storage.put(command, map);
			return;
		}

		storage.get(command).put(db.getLanguage(), db);

	}

	public static boolean containsData(final String command, final Language language) {
		if (!contains(command)) {
			return false;
		}

		return storage.get(command).containsKey(language);
	}

	public static boolean contains(final String command) {
		return storage.containsKey(command);
	}

	public static void remove(final String command) {
		storage.remove(command);
	}

	public static void clear() {
		storage.clear();
	}

}