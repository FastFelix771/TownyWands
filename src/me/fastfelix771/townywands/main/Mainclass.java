package me.fastfelix771.townywands.main;

import java.io.IOException;
import java.util.logging.Level;

import me.fastfelix771.townywands.inventory.ConfigurationParser;
import me.fastfelix771.townywands.listeners.InventoryListener;
import me.fastfelix771.townywands.metrics.Metrics;
import me.fastfelix771.townywands.utils.Database;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Mainclass extends JavaPlugin {

	private static Mainclass instance;
	private static ConfigurationParser cp;

	@Override
	public void onEnable() {
		instance = this;
		cp = new ConfigurationParser((YamlConfiguration) getConfig(), Level.INFO, true);
		Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
		getParser().parse();
		metrics(getConfig().getBoolean("metrics"));
	}

	@Override
	public void onDisable() {
		instance = null;
		cp = null;
		Database.clear();
	}

	@Override
	public void onLoad() {
		saveDefaultConfig();
	}

	public static Mainclass getInstance() {
		return instance;
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
	}

}