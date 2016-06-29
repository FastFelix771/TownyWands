package de.fastfelix771.townywands.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import org.bukkit.inventory.Inventory;
import de.fastfelix771.townywands.inventory.ModularGUI;
import de.fastfelix771.townywands.lang.Language;

public class Database {

    private static final HashMap<String, ModularGUI> storage = new HashMap<>();

    public static Collection<ModularGUI> guiList() {
        return Collections.unmodifiableCollection(storage.values());
    }

    public static Inventory get(final String command, final Language language) {
        if (!contains(command)) return null;

        return storage.get(command).get(language);
    }

    public static ModularGUI get(final String command) {
        if (!contains(command)) return null;

        return storage.get(command);
    }

    public static void add(final String command, final ModularGUI gui) {
        if (!storage.containsKey(command)) storage.put(command, gui);
    }

    public static boolean contains(final String command) {
        return storage.containsKey(command);
    }

    public static boolean contains(final String command, final Language language) {
        if (!contains(command)) return false;
        return get(command).get(language) != null;
    }

    public static void remove(final String command) {
        storage.remove(command);
    }

    public static void clear() {
        storage.clear();
    }

}