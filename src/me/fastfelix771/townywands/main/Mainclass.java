package me.fastfelix771.townywands.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import me.fastfelix771.townywands.inventory.ConfigurationParser;
import me.fastfelix771.townywands.listeners.InventoryListener;
import me.fastfelix771.townywands.listeners.TownyWands;
import me.fastfelix771.townywands.metrics.Metrics;
import me.fastfelix771.townywands.utils.Database;
import me.fastfelix771.townywands.utils.Utf8YamlConfiguration;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Mainclass extends JavaPlugin {

	private static Mainclass instance;
	private static ConfigurationParser cp;
	private static boolean translate;
	private static ExecutorService pool;
	private static int threads;
	private static File file;

	@Override
	public void onLoad() {
		saveDefaultConfig();
		saveResource("inventories.yml", false);
		file = new File(getDataFolder().getAbsolutePath() + "/inventories.yml"); // <-- Here we set the file where the inventory configuration is placed in.
	}

	@Override
	public void onEnable() {
		instance = this;
		Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
		getCommand("townywands").setExecutor(new TownyWands());

		if (getConfig().get("metrics") == null) {
			metrics(true);
		} else {
			metrics(getConfig().getBoolean("metrics"));
		}

		if (getConfig().get("auto-translate") == null) {
			translate = false;
		} else {
			translate = getConfig().getBoolean("auto-translate");
		}

		if (getConfig().get("cpu-threads") == null) {
			threads = 4;
		} else {
			threads = getConfig().getInt("cpu-threads");
		}

		getLogger().log(Level.INFO, "Auto-Translation is " + (translate ? "enabled" : "disabled"));
		getLogger().log(Level.INFO, "Using " + threads + " of " + Runtime.getRuntime().availableProcessors() + " possible threads.");

		pool = Executors.newFixedThreadPool(threads);

		cp = new ConfigurationParser(loadConfig(file), Level.INFO, true, file);
		getParser().parse();
	}

	@Override
	public void onDisable() {
		instance = null;
		cp = null;
		Database.clear();
	}

	public static void reload() {
		Database.clear();
		cp.setConfig(loadConfig(file));
		getParser().parse();
	}

	private static Utf8YamlConfiguration loadConfig(final File file) {
		final Utf8YamlConfiguration config = new Utf8YamlConfiguration();
		try {
			config.load(new FileInputStream(file));
		} catch (final Exception e) {
		}
		return config;
	}

	public static void checkVersion() {
		// Coming soon!
	}

	public static void updateConfig() {
		// Coming soon!
	}

	public static Mainclass getInstance() {
		return instance;
	}

	public static ExecutorService getPool() {
		return pool;
	}

	public static ConfigurationParser getParser() {
		return cp;
	}

	private void metrics(final boolean bool) {
		if (bool) {
			try {
				final Metrics metrics = new Metrics(this);
				metrics.start();
			} catch (final IOException e) {
				getLogger().log(Level.WARNING, "Failed to start plugin metrics! Error: " + e.getLocalizedMessage());
			}
		}
		getLogger().log(Level.INFO, "Metrics are " + (bool ? "enabled" : "disabled"));
	}

	public static boolean getAutoTranslate() {
		return translate;
	}

}