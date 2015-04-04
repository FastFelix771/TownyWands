package me.fastfelix771.townywands.main;

import java.io.File;
import java.io.IOException;

import me.fastfelix771.townywands.commands.CommandListener;
import me.fastfelix771.townywands.inventory.InvAdmin;
import me.fastfelix771.townywands.inventory.InvPlayer;
import me.fastfelix771.townywands.inventory.InvResident;
import me.fastfelix771.townywands.utils.Metrics;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public final class Mainclass extends JavaPlugin {

	private static Mainclass instance;
	public static final ConsoleCommandSender console = Bukkit.getConsoleSender();
	public static final PluginManager pm = Bukkit.getPluginManager();
	public static final BukkitScheduler sh = Bukkit.getScheduler();
	public static final Plugin towny = pm.getPlugin("Towny");
	/**
	 * @description You need to block my listeners if you want to your own guis, because TownyWands would listen on the inventory too and maybe cause trouble with your plugin...
	 * @usage Set it to false and everything is okay :)
	 */
	public static boolean allowlisteners = true;

	@Override
	public final void onEnable() {
		instance = this;
		loadMetrics();
		setupGUIs();
		console.sendMessage("§6[§3TownyWands§6]" + " §bEnabling...");
		console.sendMessage("§6[§3TownyWands§6]" + " §aFound towny version §c" + towny.getDescription().getVersion() + " §a!");
		getCommand("twa").setExecutor(new CommandListener());
		getCommand("twu").setExecutor(new CommandListener());
		pm.registerEvents(new PlayerHandler(), this);
	}

	private final void setupGUIs() {
		InvPlayer.createGUI();
		InvResident.createGUI();
		InvAdmin.createGUI();
	}

	@Override
	public final void onDisable() {
		console.sendMessage("§6[§3TownyWands§6]" + " §bDisabling...");
		instance = null;
	}

	@Override
	public final void onLoad() {
		console.sendMessage("§6[§3TownyWands§6]" + " §bLoading...");
		saveDefaultConfig();
		if (!new File(getDataFolder().getAbsolutePath() + "/messages.yml").exists()) {
			saveResource("messages.yml", true);
		}
	}

	public static final Mainclass getInstance() {
		return instance;
	}

	private final void loadMetrics() {
		if (getConfig().getBoolean("Metrics") == true) {
			try {
				final Metrics metrics = new Metrics(this);
				metrics.start();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}
}