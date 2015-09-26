package me.fastfelix771.townywands.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import me.fastfelix771.townywands.commands.CommandController;
import me.fastfelix771.townywands.commands.Commands;
import me.fastfelix771.townywands.inventory.ConfigurationParser;
import me.fastfelix771.townywands.listeners.InventoryListener;
import me.fastfelix771.townywands.metrics.Metrics;
import me.fastfelix771.townywands.utils.Database;
import me.fastfelix771.townywands.utils.Reflect;
import me.fastfelix771.townywands.utils.Reflect.Version;
import me.fastfelix771.townywands.utils.SignGUI;
import me.fastfelix771.townywands.utils.Update;
import me.fastfelix771.townywands.utils.Utf8YamlConfiguration;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Mainclass extends JavaPlugin {

	private static final long CONFIG_VERSION = 241; // Configuration Version.
	private static Mainclass instance;
	private static ConfigurationParser cp;
	private static boolean translate;
	private static ExecutorService pool;
	private static int threads;
	private static File file;
	private static boolean checkUpdates;
	private static SignGUI signgui;
	private static boolean bungeecord;

	@Override
	public void onLoad() {
		instance = this;
		saveDefaultConfig();
		saveResource("inventories.yml", false);
		file = new File(getDataFolder().getAbsolutePath() + "/inventories.yml"); // <-- Here we set the file where the inventory configuration is placed in.
	}

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
		CommandController.registerCommands(this, new Commands());

		if (getConfig().get("configVersion") != null) {
			final long version = getConfig().getLong("configVersion");

			// If the version has changed, update the config!
			if (!(CONFIG_VERSION == version)) {
				final File config = new File(getDataFolder().getAbsolutePath() + "/" + "config.yml");
				if (file != null) {
					final boolean success = config.renameTo(new File(getDataFolder().getAbsolutePath() + "/" + "config_" + version + ".yml"));
					if (!success) {
						getLogger().warning("Failed to update configuration! Continue using the old one...");
						getLogger().warning("You should try to delete the older config files with numbers behind the name!");
					} else {
						// If everything was fine, save the newest config and reload it.
						saveDefaultConfig();
						reloadConfig();
					}
				}
			}

		}

		// SignGUI is 1.8 only due to some Netty problems, i'll fix that when i get some time for it.
		if (Reflect.getServerVersion() == Version.v1_8) {
			signgui = new SignGUI(this);
			Bukkit.getPluginManager().registerEvents(signgui, this);
		} else {
			signgui = null;
		}

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

		if (getConfig().get("checkForUpdates") == null) {
			checkUpdates = false;
		} else {
			checkUpdates = getConfig().getBoolean("checkForUpdates");
		}

		if (getConfig().get("bungeecord") == null) {
			bungeecord = false;
		} else {
			bungeecord = getConfig().getBoolean("bungeecord");
		}

		if (bungeecord) {
			getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		}

		if (checkUpdates) {
			try {
				final Update update = new Update(this);
				update.check();
			} catch (final Exception e) {
				getLogger().warning("Failed to check for updates!");
			}
		}

		getLogger().log(Level.INFO, "Update-Checking is " + (getConfig().getBoolean("checkForUpdates") ? "enabled" : "disabled"));
		getLogger().log(Level.INFO, "Auto-Translation is " + (translate ? "enabled" : "disabled"));
		getLogger().log(Level.INFO, "Using " + threads + " of " + Runtime.getRuntime().availableProcessors() + " possible threads.");
		getLogger().log(Level.INFO, "SignGUI's does " + (Reflect.getServerVersion() == Version.v1_8 ? "work on this version!" : "not work on this version!"));

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
		getInstance().reloadConfig();
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

	public static SignGUI getSignGUI() {
		return signgui;
	}

	public boolean getBungeecord() {
		return bungeecord;
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