package de.fastfelix771.townywands.main;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import de.fastfelix771.townywands.commands.CommandController;
import de.fastfelix771.townywands.commands.Commands;
import de.fastfelix771.townywands.inventory.ConfigurationParser;
import de.fastfelix771.townywands.listeners.InventoryListener;
import de.fastfelix771.townywands.metrics.Metrics;
import de.fastfelix771.townywands.utils.Database;
import de.fastfelix771.townywands.utils.Reflect;
import de.fastfelix771.townywands.utils.Reflect.Version;
import de.fastfelix771.townywands.utils.SignGUI;
import de.fastfelix771.townywands.utils.Update;
import de.fastfelix771.townywands.utils.Update.Result;
import de.fastfelix771.townywands.utils.Update.State;

public final class TownyWands extends JavaPlugin implements Listener {

    @SuppressWarnings("unused")
    private static final int CONFIG_VERSION = 988; // Configuration Version.
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
    @SuppressWarnings("unused")
    private static boolean spigot;
    @Getter
    private static boolean protocolLibEnabled;

    @Override
    public void onLoad() {
        instance = this;
        getDataFolder().mkdirs();
        ConfigManager.saveResource("config.yml", new File(this.getDataFolder().getAbsolutePath() + "/config.yml"), false);
        file = new File(this.getDataFolder().getAbsolutePath() + "/inventories.yml");
        ConfigManager.saveResource("inventories.yml", file, false);
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
        Bukkit.getPluginManager().registerEvents(this, this);
        CommandController.registerCommands(this, new Commands());

        /*
         * This feature is temporary disabled, it will be back and working in the next update! if (this.getConfig().get("configVersion") != null) { final int version = this.getConfig().getInt("configVersion"); /* // If the version has changed, update the config! if (!(CONFIG_VERSION == version)) { final File config = new File(this.getDataFolder().getAbsolutePath() + "/" + "config.yml"); if (file != null && file.exists()) { final boolean success = config.renameTo(new File(this.getDataFolder().getAbsolutePath() + "/" + "config_" + version + ".yml")); if (!success) { this.getLogger().warning("Failed to update configuration! Continue using the old one..."); this.getLogger().warning("You should try to delete the older config files with numbers behind the name!"); } else { // If everything was fine, save the newest config and reload it. this.saveDefaultConfig(); this.reloadConfig(); } } } }
         */

        protocolLibEnabled = Bukkit.getPluginManager().getPlugin("ProtocolLib") != null && Bukkit.getPluginManager().isPluginEnabled("ProtocolLib");

        if (Reflect.getServerVersion() == Version.v1_8 || Reflect.getServerVersion() == Version.v1_7) {
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

        if (this.getConfig().get("spigot") == null) spigot = false;
        else spigot = this.getConfig().getBoolean("spigot");

        if (bungeecord) this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        if (checkUpdates) try { // Make async.
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
        if (protocolLibEnabled) this.getLogger().log(Level.INFO, "Using ProtocolLib instead of TownyWands' internal methods to modify packets.");
        // TODO: Add a spigot notification

        pool = Executors.newFixedThreadPool(threads);

        parser = new ConfigurationParser(ConfigManager.loadConfig(file), Level.INFO, true, file);
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
        parser.setConfig(YamlConfiguration.loadConfiguration(file));
        getParser().getInventoryTokens().clear();
        getParser().parse();
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
    public void onJoin(PlayerJoinEvent e) { // Temporary event listener, the updater class needs some enhancements.
        Player p = e.getPlayer();
        if ((p.isOp() || p.hasPermission("townywands.msg.update")) && (checkUpdates && updateResult != null && (updateResult.getState() == State.UPDATE_FOUND))) {
            p.sendMessage("§4!UPDATE! §6-> TownyWands has found an update!");
            p.sendMessage("§4!UPDATE! §6-> You are currently on version §c" + getDescription().getVersion());
            if (Reflect.getServerVersion() != Version.v1_8) {
                p.sendMessage("§4!UPDATE! §6-> Download latest: §a" + updateResult.getLatestDownload());
                return;
            }

        }
    }

}