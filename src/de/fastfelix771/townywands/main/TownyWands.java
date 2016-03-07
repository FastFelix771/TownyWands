package de.fastfelix771.townywands.main;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import de.fastfelix771.townywands.commands.CommandController;
import de.fastfelix771.townywands.commands.Commands;
import de.fastfelix771.townywands.inventory.ConfigurationParser;
import de.fastfelix771.townywands.listeners.TownyWandsListener;
import de.fastfelix771.townywands.metrics.Metrics;
import de.fastfelix771.townywands.utils.Database;
import de.fastfelix771.townywands.utils.Invoker;
import de.fastfelix771.townywands.utils.Reflect;
import de.fastfelix771.townywands.utils.Reflect.Version;
import de.fastfelix771.townywands.utils.Updater;
import de.fastfelix771.townywands.utils.Updater.Result;
import de.fastfelix771.townywands.utils.vSign;

@Log
public final class TownyWands extends JavaPlugin {

    private static final int CONFIG_VERSION = 1799;
    @Getter private static TownyWands instance;
    @Getter private static ConfigurationParser parser;
    @Getter private static boolean autotranslate;
    @Getter private static ExecutorService pool;
    @Getter private static vSign vSign;
    @Getter private static boolean bungeecord;
    @Getter private static boolean protocolLibEnabled;
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

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new TownyWandsListener(), this);
        CommandController.registerCommands(this, new Commands());

        protocolLibEnabled = Bukkit.getPluginManager().getPlugin("ProtocolLib") != null && Bukkit.getPluginManager().isPluginEnabled("ProtocolLib");

        if (Reflect.getServerVersion() == Version.v1_9 || Reflect.getServerVersion() == Version.v1_8 || Reflect.getServerVersion() == Version.v1_7) {
            vSign = new vSign(this);
        } else vSign = null;

        if (this.getConfig().get("metrics") == null) this.metrics(true);
        else this.metrics(this.getConfig().getBoolean("metrics"));

        if (this.getConfig().get("auto-translate") == null) autotranslate = false;
        else autotranslate = this.getConfig().getBoolean("auto-translate");

        if (this.getConfig().get("cpu-threads") == null) threads = 4;
        else threads = this.getConfig().getInt("cpu-threads");

        if (this.getConfig().get("checkForUpdates") == null) updateCheckingEnabled = false;
        else updateCheckingEnabled = this.getConfig().getBoolean("checkForUpdates");

        if (this.getConfig().get("bungeecord") == null) bungeecord = false;
        else bungeecord = this.getConfig().getBoolean("bungeecord");

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
        log.info("vSign's does " + (vSign != null ? "work on this version!" : "not work on this version!"));
        if (protocolLibEnabled) log.info("Using ProtocolLib instead of TownyWands' internal methods to modify packets.");

        pool = Executors.newFixedThreadPool(threads);

        parser = new ConfigurationParser(ConfigManager.loadYAML(new File(this.getDataFolder().getAbsolutePath() + "/inventories.yml")), Level.WARNING, true, new File(this.getDataFolder().getAbsolutePath() + "/inventories.yml"));
        getParser().parse();

        loadAddons();
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
        parser.setConfig(YamlConfiguration.loadConfiguration(new File(getInstance().getDataFolder().getAbsolutePath() + "/inventories.yml")));
        getParser().getInventoryTokens().clear();
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
        if(!file.exists()) return; // Safety first.

        if(file.renameTo(new File(getDataFolder().getAbsolutePath().concat(String.format("/config_%d.yml", currentVersion))))) {
            saveDefaultConfig();
            reloadConfig();
            return;
        }
        
        log.severe("Renaming of the old configuration file has failed, continue using the old one...");

    }

    // Coming Soon!
    private void loadAddons() {}

}