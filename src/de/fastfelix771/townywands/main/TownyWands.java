package de.fastfelix771.townywands.main;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.persistence.PersistenceException;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import de.fastfelix771.townywands.commands.CommandController;
import de.fastfelix771.townywands.commands.Commands;
import de.fastfelix771.townywands.dao.EntityGUI;
import de.fastfelix771.townywands.dao.EntityInventory;
import de.fastfelix771.townywands.dao.EntityItem;
import de.fastfelix771.townywands.inventory.HybridParser;
import de.fastfelix771.townywands.listeners.TownyWandsListener;
import de.fastfelix771.townywands.metrics.Metrics;
import de.fastfelix771.townywands.packets.PacketHandler;
import de.fastfelix771.townywands.packets.VirtualSign;
import de.fastfelix771.townywands.utils.Invoker;
import de.fastfelix771.townywands.utils.Reflect;
import de.fastfelix771.townywands.utils.Updater;
import de.fastfelix771.townywands.utils.Updater.Result;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

@Log(topic = "TownyWands")
public final class TownyWands extends JavaPlugin {

	private static final List<Class<?>> databaseClasses = Arrays.asList(new Class<?>[] { EntityItem.class, EntityInventory.class, EntityGUI.class });

	private static final int CONFIG_VERSION = 1800;

	@Getter 
	private static TownyWands instance;

	@Getter 
	private static boolean autotranslate;

	@Getter 
	private static ExecutorService pool;

	@Getter 
	private static PacketHandler packetHandler = Reflect.getServerVersion().getPacketHandler();

	@Getter 
	private static VirtualSign virtualSign = Reflect.getServerVersion().getVirtualSign();

	@Getter 
	private static boolean bungeecord;

	@Getter 
	private static boolean protocolLibEnabled;

	@Getter 
	private static boolean updateCheckingEnabled;

	@Getter @Setter(value=AccessLevel.PRIVATE) 
	private static Result updateResult;

	private static int threads;

	@Override
	public void onLoad() {
		instance = this;
		getDataFolder().mkdirs();
		ConfigManager.saveResource("config.yml", new File(this.getDataFolder().getAbsolutePath() + "/config.yml"), false);
		ConfigManager.saveResource("inventories.yml", new File(this.getDataFolder().getAbsolutePath() + "/inventories.yml"), false);

		updateConfig();
	}

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(new TownyWandsListener(), this);
		CommandController.registerCommands(this, new Commands());

		log.info("vSign's does ".concat((virtualSign != null ? "work on this version!".concat(String.format(" (Using: %s)", Reflect.getServerVersion().toString())) : String.format("not work on this version! (Detected: %s)", Reflect.getServerVersion().toString()))));

		metrics(this.getConfig().getBoolean("metrics"));
		autotranslate = this.getConfig().getBoolean("auto-translate");
		threads = this.getConfig().getInt("cpu-threads");
		updateCheckingEnabled = this.getConfig().getBoolean("checkForUpdates");
		bungeecord = this.getConfig().getBoolean("bungeecord");

		if (bungeecord) this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

		if (updateCheckingEnabled) new Updater(this, 89537).check(new Invoker<Result>() {

			@Override
			public void invoke(Result result) {
				setUpdateResult(result);
			}

		});;

		log.info("Update-Checking is " + (updateCheckingEnabled ? "enabled" : "disabled"));
		log.info("Auto-Translation is " + (autotranslate ? "enabled" : "disabled"));
		log.info("Using " + threads + " of " + Runtime.getRuntime().availableProcessors() + " threads.");

		pool = Executors.newFixedThreadPool(threads);

		setupDatabase();
	}

	@Override
	public void onDisable() {
		instance = null;
	}

	public static void reload() {
		getInstance().reloadConfig();
		autotranslate = getInstance().getConfig().getBoolean("auto-translate");
		threads = getInstance().getConfig().getInt("cpu-threads");
		updateCheckingEnabled = getInstance().getConfig().getBoolean("checkForUpdates");
		bungeecord = getInstance().getConfig().getBoolean("bungeecord");
	}

	private void metrics(boolean bool) {
		if (bool) try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		}
		catch (IOException e) {
			log.warning("Failed to start plugin metrics! Error: " + e.getLocalizedMessage());
		}
		log.info("Metrics are " + (bool ? "enabled" : "disabled"));
	}

	private void setupDatabase() {
		try {
			for(Class<?> clazz : databaseClasses) {
				getDatabase().find(clazz).findRowCount();
			}
			log.info("Database found! - Loading inventory configuration from database...");
		} catch (PersistenceException ex) {
			log.info("Installing database for TownyWands due to first time usage");
			log.info("Inventory configuration will be loaded from config file!");
			installDDL();

			new HybridParser(ConfigManager.loadYAML(new File(this.getDataFolder().getAbsolutePath() + "/inventories.yml"))).parse();
		}
	}

	private void updateConfig() {
		saveDefaultConfig();

		int currentVersion = getConfig().getInt("configVersion");
		if(currentVersion == CONFIG_VERSION) return;

		log.info(String.format("%s configuration file...", currentVersion < CONFIG_VERSION ? "Updating" : "Downgrading"));

		File file = new File(getDataFolder().getAbsolutePath().concat("/config.yml"));
		if(!file.exists()) return;

		if(file.renameTo(new File(getDataFolder().getAbsolutePath().concat(String.format("/config_%d.yml", currentVersion))))) {
			saveDefaultConfig();
			reloadConfig();
			return;
		}

		log.severe("Renaming of the old configuration file has failed, continue using the old one...");
	}

	@Override
	public List<Class<?>> getDatabaseClasses() {
		return databaseClasses;
	}

}