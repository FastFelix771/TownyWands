package me.fastfelix771.townywands.inventory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import me.fastfelix771.townywands.lang.Language;
import me.fastfelix771.townywands.lang.Translator;
import me.fastfelix771.townywands.main.TownyWands;
import me.fastfelix771.townywands.utils.Database;
import me.fastfelix771.townywands.utils.Utf8YamlConfiguration;
import me.fastfelix771.townywands.utils.Utils;
import me.fastfelix771.townywands.utils.Utils.Type;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ConfigurationParser {

    private Utf8YamlConfiguration config;
    private final Level lvl;
    private final boolean async;// Adde consolen commands für jedes item!
    private boolean error;// Und ein paar spielerbezogene variablen wie {uuid} , {username} undso, damit man consolen-commands auch brauchen kann! Und diese dem Config-How to beifügen.
    private final File file;
    private final List<String> tokens;

    public ConfigurationParser(final Utf8YamlConfiguration config, final Level loglevel, final boolean async, final File file) {
        Validate.notNull(config, "config cannot be null");
        Validate.notNull(loglevel, "loglevel cannot be null");
        Validate.notNull(file, "file cannot be null!");
        this.config = config;
        this.lvl = loglevel;
        this.async = async;
        this.file = file;
        this.error = false;
        this.tokens = new ArrayList<String>();
    }

    public Utf8YamlConfiguration getConfig() {
        return this.config;
    }

    public void setConfig(final Utf8YamlConfiguration config) {
        this.config = config;
    }

    public boolean parse() {
        final ConfigurationSection sec_inventories = this.config.getConfigurationSection("inventories");
        final boolean refreshTranslations = this.config.getBoolean("refresh-Translations");

        Validate.notNull(sec_inventories, "Section 'inventories' cannot be null!");
        Validate.notNull(refreshTranslations, "Boolean 'refresh-Translations' cannot be null!");
        this.config.set("refresh-Translations", false);

        final Map<String, Object> inventories = sec_inventories.getValues(false);

        for (final String str_name : inventories.keySet()) {
            final Runnable invJob = new Runnable() {

                @Override
                public void run() {
                    final long start = System.currentTimeMillis();
                    Bukkit.getConsoleSender().sendMessage(refreshTranslations ? "§cTownyWands | §bSetup and refreshing of inventory §3" + str_name + " §bhas started" : "§cTownyWands | §bSetup of inventory §3" + str_name + " §bhas started");
                    final ConfigurationSection inv = sec_inventories.getConfigurationSection(str_name);

                    if (refreshTranslations && !TownyWands.isAutotranslate()) ConfigurationParser.this.error("AutoTranslation is disabled, cannot refresh the translations.");

                    final String name = inv.getString("name"); // Name of the inventory
                    final int slots = inv.getInt("slots"); // Slotcount...obviously
                    final String command = inv.getString("command");
                    final String permission = inv.getString("permission"); // permission needed to open the inventory
                    final ConfigurationSection items = inv.getConfigurationSection("items"); // contents of the inventory

                    if (name.length() > 32) {
                        ConfigurationParser.this.error("Inventory '" + str_name + "' is wrong configurated! Field 'name' has an invalid value.\n" + "It needs to be 32 or less characters due to limitations of minecraft.");
                        return;
                    }

                    if (!Utils.isValidSlotCount(slots)) {
                        ConfigurationParser.this.error("Inventory '" + str_name + "' is wrong configurated! Field 'slots' has an invalid value.\n" + "It needs to be 9,18,27,36,45 or 54 due to limitations of minecraft.");
                        return;
                    }

                    if (items == null) {
                        ConfigurationParser.this.error("Inventory '" + str_name + "' is wrong configurated! ConfigurationSection 'items' does not exist.");
                        return;
                    }

                    if (command.replace(" ", "").equalsIgnoreCase("")) {
                        ConfigurationParser.this.error("Inventory '" + str_name + "' is wrong configurated! Field 'command' cannot be empty.");
                        return;
                    }

                    final Map<String, Object> item_values = items.getValues(false);

                    // Store some temporary data here.
                    final ModularGUI gui = new ModularGUI(str_name, command, permission);
                    boolean saveMe = false;

                    for (final String item_name : item_values.keySet()) {
                        final ConfigurationSection i = items.getConfigurationSection(item_name);

                        if ((i.get("itemID") == null) || (i.get("metaID") == null)) {
                            ConfigurationParser.this.error("Item '" + item_name + "' is wrong configured! The field 'itemID' or 'metaID' does not exist!");
                            continue;
                        }

                        final int id = i.getInt("itemID");
                        final int metaid = i.getInt("metaID");
                        @SuppressWarnings("deprecation")
                        final Material material = Material.getMaterial(id);

                        // Skip to next item if the given material doesnt exist.
                        if (material == null) {
                            ConfigurationParser.this.error("Item '" + item_name + "' is wrong configured! The field 'itemID' has an invalid value.");
                            continue;
                        }

                        if (i.get("slot") == null) {
                            ConfigurationParser.this.error("Item '" + item_name + "' is wrong configured! The field 'slot' doesnt exist!");
                            continue;
                        }

                        final int slot = i.getInt("slot") - 1;

                        if (i.get("quantity") == null) {
                            ConfigurationParser.this.error("Item '" + item_name + "' is wrong configured! The field 'quantity' doesnt exist!");
                            continue;
                        }

                        if ((i.get("console_commands") == null) && (i.get("commands") == null)) {
                            ConfigurationParser.this.error("Item '" + item_name + "' is wrong configured! The field 'commands' and 'console_commands' doesnt exist!");
                            continue;
                        }

                        List<String> commands = new ArrayList<String>();
                        List<String> console_commands = new ArrayList<String>();

                        if (i.get("console_commands") != null) console_commands = i.getStringList("console_commands");

                        if (i.get("commands") != null) commands = i.getStringList("commands");

                        final int quantity = i.getInt("quantity");
                        final boolean enchanted = i.getBoolean("enchanted");

                        for (final Language lang : Language.values()) {

                            final String langcode = lang.getCode();

                            String iname = i.getString("name_" + langcode);
                            List<String> ilore = i.getStringList("lore_" + langcode);
                            boolean translate = refreshTranslations;

                            // Skip to next language if one or more of the parameters are missing and auto-translating is disabled.
                            if ((iname == null) || (ilore == null)) {
                                if (!TownyWands.isAutotranslate()) continue;
                                final String engcode = Language.ENGLISH.getCode();
                                iname = i.getString("name_" + engcode);
                                ilore = i.getStringList("lore_" + engcode);
                                // Check if english values exist...just to be safe
                                if ((iname == null) || (ilore == null)) continue;
                                translate = true;
                            }

                            // Color :3
                            if (!translate) {
                                iname = ChatColor.translateAlternateColorCodes('&', iname);
                                List<String> newLore = new ArrayList<String>();
                                for (String l : ilore) {
                                    l = ChatColor.translateAlternateColorCodes('&', l);
                                    newLore.add(l);
                                }
                                ilore = newLore;
                                newLore = null;
                            }

                            // Lets translate it here & save it. (commands will not get auto-translated...that would be a desaster :D)
                            // ColorCoding doesnt work currently on fresh translated stuff, so i give them some basic color-code after the translation.
                            if (translate) {
                                iname = Translator.translate(Language.ENGLISH, lang, iname);
                                i.set("name_" + langcode, StringEscapeUtils.unescapeJava("&6&l" + iname));
                                iname = "§6§l" + iname;
                                final List<String> saveLore = new ArrayList<String>();
                                for (int ic = 0; ic < ilore.size(); ic++) {
                                    String text = ilore.get(ic);
                                    text = Translator.translate(Language.ENGLISH, lang, text);
                                    saveLore.add(StringEscapeUtils.escapeJava("&2&l" + text));
                                    text = "§2§l" + text;
                                    ilore.set(ic, text);
                                }
                                i.set("lore_" + langcode, saveLore);
                                saveMe = true;
                            }

                            Inventory iinv = null;

                            if (gui.contains(lang)) iinv = gui.get(lang);
                            else {
                                iinv = Bukkit.createInventory(null, slots, ChatColor.translateAlternateColorCodes('&', name));
                                gui.add(lang, iinv);
                            }

                            // Create the item itself, configurate it and add it to the GUI.
                            ItemStack iitem = new ItemStack(material, quantity, (short) metaid);
                            final ItemMeta meta = iitem.getItemMeta();
                            meta.setDisplayName(iname);
                            meta.setLore(ilore);
                            iitem.setItemMeta(meta);

                            iitem = Utils.setCommands(iitem, commands, Type.PLAYER);
                            iitem = Utils.setCommands(iitem, console_commands, Type.CONSOLE);
                            iitem = Utils.setKey(iitem, command);

                            if (enchanted) iitem = Utils.addEnchantmentGlow(iitem);

                            iitem = Utils.hideFlags(iitem);
                            iinv.setItem(slot, iitem);

                        }
                    }

                    Database.add(command, gui);

                    final long end = System.currentTimeMillis();
                    if (saveMe) ConfigurationParser.this.save();
                    ConfigurationParser.this.tokens.add(str_name);
                    Bukkit.getConsoleSender().sendMessage(refreshTranslations ? "§cTownyWands | §bSetup and refreshing of inventory §3" + str_name + " §btook §3" + (end - start) + "§bms" : "§cTownyWands | §bSetup of inventory §3" + str_name + " §btook §3" + (end - start) + "§bms");
                }
            };
            if (this.async) TownyWands.getPool().execute(invJob);
            else invJob.run();
        }

        if (refreshTranslations) this.save();
        final boolean err = this.error;
        this.error = false; // Resetting error after parsing to make the parser re-usable.

        return err;
    }

    public List<String> getInventoryTokens() {
        return this.tokens;
    }

    private void save() {
        try {
            this.config.save(this.file);
        }
        catch (final IOException e) {
            this.error("Failed to save configuration file " + this.file.getName());
        }
    }

    private void error(final String message) {
        this.error = true;
        TownyWands.getInstance().getLogger().log(this.lvl, message);
    }

}