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
package de.fastfelix771.townywands.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.bind.JAXBException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import de.fastfelix771.townywands.commands.CommandController;
import de.fastfelix771.townywands.commands.Commands;
import de.fastfelix771.townywands.files.Config;
import de.fastfelix771.townywands.inventory.HybridParser;
import de.fastfelix771.townywands.listeners.TownyWandsListener;
import de.fastfelix771.townywands.metrics.Metrics;
import de.fastfelix771.townywands.utils.Documents;
import de.fastfelix771.townywands.utils.Updater;
import de.fastfelix771.townywands.utils.Updater.Result;
import de.unitygaming.bukkit.vsign.Version;
import de.unitygaming.bukkit.vsign.api.vSignAPI;
import de.unitygaming.bukkit.vsign.util.Invoker;
import lombok.Getter;
import lombok.extern.java.Log;

@Log(topic = "TownyWands")
public final class TownyWands extends JavaPlugin {

	@Getter 
	private static TownyWands instance;

	@Getter
	private static Config configuration;

	@Getter 
	private static ExecutorService pool;

	@Getter 
	private static vSignAPI signAPI;

	@Getter
	private volatile static Result updateResult;

	@Override
	public void onLoad() {
		instance = this;
		getDataFolder().mkdirs();

		updateConfig();
		
		File file = Paths.get(this.getDataFolder().getAbsolutePath(), "inventories.yml").toFile();
		ConfigManager.saveResource("inventories.yml", file, false);
	}

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(new TownyWandsListener(), this);
		CommandController.registerCommands(this, new Commands());

		File file = Paths.get(this.getDataFolder().getAbsolutePath(), "inventories.yml").toFile();
		YamlConfiguration inventories = ConfigManager.loadYAML(file);
		new HybridParser(inventories, file).parse();

		log.info("vSign's does ".concat(vSignAPI.check() ? "work on this version! " : "not work on this version! ").concat("Version: " + Version.getCurrent().toString()));
		signAPI = new vSignAPI(this);

		checkUpdates();
		setupMetrics();
		setupBungee();

		log.info("Update-Checking is " + (configuration.updateChecking ? "enabled" : "disabled"));
		log.info("Auto-Translation is " + (configuration.autoTranslate ? "enabled" : "disabled"));
		log.info("Using " + configuration.threads + " of " + Runtime.getRuntime().availableProcessors() + " threads.");

		pool = Executors.newFixedThreadPool(configuration.threads);
	}

	@Override
	public void onDisable() {
		instance = null;
	}

	private void setupMetrics() {
		if (configuration.useMetrics) try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		}
		catch (IOException e) {
			log.warning("Failed to start plugin metrics! Error: " + e.getLocalizedMessage());
		}
		log.info("Metrics are " + (configuration.useMetrics ? "enabled" : "disabled"));
	}

	private void updateConfig() {
		loadConfig();

		int currentVersion = new Config().version;
		if(currentVersion == configuration.version) return;

		log.info(String.format("%s configuration file...", currentVersion < configuration.version ? "Downgrading" : "Upgrading"));

		File file = Paths.get("configs", "configuration.xml").toFile();
		File dest = Paths.get("configs", String.format("configuration_%d.xml", currentVersion)).toFile();

		if(file.renameTo(dest)) loadConfig();
	}

	private void loadConfig() {
		try {
			Documents.saveDefault("configs", "configuration", new Config());
			configuration = (Config) Documents.load("configs", "configuration", Config.class);
		} catch (JAXBException e) {
			e.printStackTrace();

			log.warning("Failed to read or create the configuration file!");
			log.warning("Using the default configuration...");

			configuration = new Config();
		}
	}

	private void checkUpdates() {
		if (configuration.updateChecking) {
			new Updater(this, 89537).check(new Invoker<Result>() {

				@Override
				public void invoke(Result result) {
					updateResult = result;
				}

			});
		}
	}

	private void setupBungee() {
		if (configuration.bungee) this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
	}

}
