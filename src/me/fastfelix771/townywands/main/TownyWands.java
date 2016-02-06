package me.fastfelix771.townywands.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import lombok.Getter;
import lombok.NonNull;
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
import me.fastfelix771.townywands.utils.Update.Result;
import me.fastfelix771.townywands.utils.Update.State;
import me.fastfelix771.townywands.utils.Utf8YamlConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class TownyWands extends JavaPlugin implements Listener {

    private static final long CONFIG_VERSION = 746; // Configuration Version.
    @Getter
    private static TownyWands instance;
    @Getter
    private static ConfigurationParser parser;
    @Getter
    private static boolean autotranslate;
    @Getter
    private static ExecutorService pool;
    private static int threads;
    private static File file;
    private static boolean checkUpdates;
    @Getter
    private static SignGUI signGUI;
    @Getter
    private static boolean bungeecord;
    private static Result updateResult; // temporary only!

    @Override
    public void onLoad() {
        instance = this;
        this.saveDefaultConfig();
        this.saveResource("inventories.yml", false);
        file = new File(this.getDataFolder().getAbsolutePath() + "/inventories.yml");
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
        Bukkit.getPluginManager().registerEvents(this, this);
        CommandController.registerCommands(this, new Commands());

        if (this.getConfig().get("configVersion") != null) {
            final long version = this.getConfig().getLong("configVersion");

            // If the version has changed, update the config!
            if (!(CONFIG_VERSION == version)) {
                final File config = new File(this.getDataFolder().getAbsolutePath() + "/" + "config.yml");
                if (file != null) {
                    final boolean success = config.renameTo(new File(this.getDataFolder().getAbsolutePath() + "/" + "config_" + version + ".yml"));
                    if (!success) {
                        this.getLogger().warning("Failed to update configuration! Continue using the old one...");
                        this.getLogger().warning("You should try to delete the older config files with numbers behind the name!");
                    }
                    else {
                        // If everything was fine, save the newest config and reload it.
                        this.saveDefaultConfig();
                        this.reloadConfig();
                    }
                }
            }

        }

        // SignGUI is 1.8 only due to some Netty problems, i'll fix that when i get some time for it. //TODO: fix it via. Reflection.
        if (Reflect.getServerVersion() == Version.v1_8) {
            signGUI = new SignGUI(this);
            Bukkit.getPluginManager().registerEvents(signGUI, this);
        }
        else signGUI = null;

        if (this.getConfig().get("metrics") == null) this.metrics(true);
        else this.metrics(this.getConfig().getBoolean("metrics"));

        if (this.getConfig().get("auto-translate") == null) autotranslate = false;
        else autotranslate = this.getConfig().getBoolean("auto-translate");

        if (this.getConfig().get("cpu-threads") == null) threads = 4;
        else threads = this.getConfig().getInt("cpu-threads");

        if (this.getConfig().get("checkForUpdates") == null) checkUpdates = false;
        else checkUpdates = this.getConfig().getBoolean("checkForUpdates");

        if (this.getConfig().get("bungeecord") == null) bungeecord = false;
        else bungeecord = this.getConfig().getBoolean("bungeecord");

        if (bungeecord) this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        if (checkUpdates) try {
            final Update update = new Update(this);
            updateResult = update.check();
        }
        catch (final Exception e) {
            this.getLogger().warning("Failed to check for updates!");
        }

        this.getLogger().log(Level.INFO, "Update-Checking is " + (checkUpdates ? "enabled" : "disabled"));
        this.getLogger().log(Level.INFO, "Auto-Translation is " + (autotranslate ? "enabled" : "disabled"));
        this.getLogger().log(Level.INFO, "Using " + threads + " of " + Runtime.getRuntime().availableProcessors() + " threads.");
        this.getLogger().log(Level.INFO, "SignGUI's does " + (signGUI != null ? "work on this version!" : "not work on this version!"));

        pool = Executors.newFixedThreadPool(threads);

        parser = new ConfigurationParser(loadConfig(file), Level.INFO, true, file);
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
        parser.setConfig(loadConfig(file));
        getParser().getInventoryTokens().clear();
        getParser().parse();
    }

    private static Utf8YamlConfiguration loadConfig(@NonNull File file) {
        final Utf8YamlConfiguration config = new Utf8YamlConfiguration();
        try {
            config.load(new FileInputStream(file));
        }
        catch (final Exception e) {
        }
        return config;
    }

    private void metrics(final boolean bool) {
        if (bool) try {
            final Metrics metrics = new Metrics(this);
            metrics.start();
        }
        catch (final IOException e) {
            this.getLogger().log(Level.WARNING, "Failed to start plugin metrics! Error: " + e.getLocalizedMessage());
        }
        this.getLogger().log(Level.INFO, "Metrics are " + (bool ? "enabled" : "disabled"));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if ((p.isOp() || p.hasPermission("townywands.msg.update")) && (checkUpdates && updateResult != null && (updateResult.getState() == State.UPDATE_FOUND))) {
            p.sendMessage("§4!UPDATE! §6-> TownyWands has found an update!");
            p.sendMessage("§4!UPDATE! §6-> You are currently on version §c" + getDescription().getVersion());
            p.sendMessage("§4!UPDATE! §6-> Download latest: §a" + updateResult.getLatestDownload());
        }
    }

}