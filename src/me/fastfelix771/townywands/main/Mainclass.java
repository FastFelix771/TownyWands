package me.fastfelix771.townywands.main;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public final class Mainclass extends JavaPlugin {

	private static Mainclass instance;
	public static final ConsoleCommandSender console = Bukkit.getConsoleSender();
	public static final PluginManager pm = Bukkit.getPluginManager();
	public static final BukkitScheduler sh = Bukkit.getScheduler();

	@Override
	public final void onEnable() {
		instance = this;
		console.sendMessage("§6[§3TownyWands§6]" + " §bEnabling...");
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
		if (!new File(getDataFolder().getAbsolutePath() + "/inventories.yml").exists()) {
			saveResource("inventories.yml", true);
		}
	}

	public static final Mainclass getInstance() {
		return instance;
	}
}