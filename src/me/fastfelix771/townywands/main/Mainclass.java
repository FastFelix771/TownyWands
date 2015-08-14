package me.fastfelix771.townywands.main;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

public class Mainclass extends JavaPlugin {

	private static Mainclass instance;

	@Override
	public void onEnable() {
		instance = this;
	}

	@Override
	public void onDisable() {
		instance = null;
	}

	@Override
	public void onLoad() {
		saveDefaultConfig();
		if (!new File(getDataFolder().getAbsolutePath() + "/messages.yml").exists()) {
			saveResource("messages.yml", true);
		}
	}

	public static Mainclass getInstance() {
		return instance;
	}
}