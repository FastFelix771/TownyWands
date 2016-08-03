package de.fastfelix771.townywands.main;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import de.fastfelix771.townywands.commands.CommandController;
import de.fastfelix771.townywands.commands.Commands;
import de.fastfelix771.townywands.inventory.ConfigurationParser;
import de.fastfelix771.townywands.listeners.TownyWandsListener;
import de.fastfelix771.townywands.metrics.Metrics;
import de.fastfelix771.townywands.utils.Database;
import de.fastfelix771.townywands.utils.Updater;
import de.fastfelix771.townywands.utils.Updater.Result;
import de.unitygaming.bukkit.vsign.Version;
import de.unitygaming.bukkit.vsign.api.vSignAPI;
import de.unitygaming.bukkit.vsign.invoker.Invoker;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.java.Log;

@Log(topic = "TownyWands")
public final class TownyWands extends JavaPlugin {

	private static final int CONFIG_VERSION = 1800;
	@Getter private static TownyWands instance;
	@Getter private static ConfigurationParser parser;
	@Getter private static boolean autotranslate;
	@Getter private static ExecutorService pool;
	@Getter private static vSignAPI signAPI;
	@Getter private static boolean bungeecord;
	@Getter private static boolean updateCheckingEnabled;
	@Getter @Setter(value=AccessLevel.PRIVATE) private static Result updateResult;
	private static int threads;

	@Override
	public void onLoad() {
		instance = this;
		getDataFolder().mkdirs();
		ConfigManager.saveResource("config.yml", new File(this.getDataFolder().getAbsolutePath() + "/config.yml"), false);
		ConfigManager.saveResource("inventories.yml", new File(this.getDataFolder().getAbsolutePath() + "/inventories.yml"), false);

		updateConfig();
	}

	@Override @SneakyThrows
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(new TownyWandsListener(), this);
		CommandController.registerCommands(this, new Commands());

		signAPI = new vSignAPI(this);
		log.info("vSign's does ".concat(vSignAPI.check() ? "work on this version! " : "not work on this version! ").concat("Version: ".concat(Version.getCurrent().toString())));

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

		parser = new ConfigurationParser(ConfigManager.loadYAML(new File(this.getDataFolder().getAbsolutePath() + "/inventories.yml")), Level.WARNING, true, new File(this.getDataFolder().getAbsolutePath() + "/inventories.yml"));
		getParser().parse();
	}
	
	@Override
	public void onDisable() {
		parser = null;
		Database.clear();
		instance = null;
	}

	public static void reload() {
		Database.clear();
		getInstance().reloadConfig();
		getParser().getInventoryTokens().clear();
		parser.setConfig(YamlConfiguration.loadConfiguration(new File(getInstance().getDataFolder().getAbsolutePath() + "/inventories.yml")));
		getParser().parse();
	}

	private void metrics(final boolean bool) {
		if (bool) try {
			final Metrics metrics = new Metrics(this);
			metrics.start();
		}
		catch (final IOException e) {
			log.warning("Failed to start plugin metrics! Error: " + e.getLocalizedMessage());
		}
		log.info("Metrics are " + (bool ? "enabled" : "disabled"));
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

}