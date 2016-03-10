package de.fastfelix771.townywands.main;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import de.fastfelix771.townywands.commands.CommandController;
import de.fastfelix771.townywands.commands.Commands;
import de.fastfelix771.townywands.inventory.ConfigurationParser;
import de.fastfelix771.townywands.listeners.TownyWandsListener;
import de.fastfelix771.townywands.metrics.Metrics;
import de.fastfelix771.townywands.packets.PacketHandler;
import de.fastfelix771.townywands.packets.PacketSupport;
import de.fastfelix771.townywands.packets.VirtualSign;
import de.fastfelix771.townywands.utils.Database;
import de.fastfelix771.townywands.utils.Invoker;
import de.fastfelix771.townywands.utils.Reflect;
import de.fastfelix771.townywands.utils.Updater;
import de.fastfelix771.townywands.utils.Updater.Result;

@Log(topic = "TownyWands")
public final class TownyWands extends JavaPlugin {

    private static final int CONFIG_VERSION = 1799;
    @Getter private static TownyWands instance;
    @Getter private static ConfigurationParser parser;
    @Getter private static boolean autotranslate;
    @Getter private static ExecutorService pool;

    @Getter private static VirtualSign virtualSign;
    @Getter private static PacketHandler packetHandler;

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

    @Override @SneakyThrows
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new TownyWandsListener(), this);
        CommandController.registerCommands(this, new Commands());

        protocolLibEnabled = Bukkit.getPluginManager().getPlugin("ProtocolLib") != null && Bukkit.getPluginManager().isPluginEnabled("ProtocolLib");

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

        loadAddons();

        /// EXPERIMENTAL ///

        PacketSupport ps = PacketSupport.forVersion(Reflect.getServerVersion());

        if(ps != PacketSupport.NONE) {
            Class<?> vsignclazz = Reflect.getClass(String.format("de.fastfelix771.townywands.packets.%s.%s", Reflect.getServerVersion().toString(), ((ps == PacketSupport.BOTH || ps == PacketSupport.ProtocolLib) && protocolLibEnabled) ? "ProtocolLibvSign" : "NMSvSign"));
            if(vsignclazz != null) {
                virtualSign = (VirtualSign) vsignclazz.newInstance();
            }

            Class<?> packethandlerclazz = Reflect.getClass(String.format("de.fastfelix771.townywands.packets.%s.%s", Reflect.getServerVersion().toString(), ((ps == PacketSupport.BOTH || ps == PacketSupport.ProtocolLib) && protocolLibEnabled) ? "ProtocolLibHandler" : "NMSHandler"));
            if(packethandlerclazz != null) {
                packetHandler = (PacketHandler) packethandlerclazz.newInstance();
            }
        }

        log.info("vSign's does ".concat((virtualSign != null ? "work on this version!".concat(String.format(" (Using: %s %s)", ((ps == PacketSupport.BOTH || ps == PacketSupport.ProtocolLib) && protocolLibEnabled ? "ProtocolLib" : "NMS"), Reflect.getServerVersion().toString())) : String.format("not work on this version! (Detected: %s)", Reflect.getServerVersion().toString()))));
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