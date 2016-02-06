package me.fastfelix771.townywands.inventory;

import java.util.EnumMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.fastfelix771.townywands.lang.Language;
import me.fastfelix771.townywands.main.TownyWands;
import me.fastfelix771.townywands.utils.Database;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;

// IDEA! Ingame Inventory & Item Editor... that would allow new ways of storage and would not need a ConfigurationParser anymore!
// /modify <key> <value> - ex. /modify enchanted true , this would modify the item in the players hand. Use the NBT Tricks to store most data.
// This could become difficult with multi-language GUIs...
// Other great IDEA! I should stop filling my code with notes.. that spoilers every guy on GitHub ._.

@EqualsAndHashCode(
    exclude = "guis")
@RequiredArgsConstructor
public class ModularGUI implements Cloneable {

    @NonNull
    public final String internalName;
    @Getter
    @Setter
    @NonNull
    private String command;
    @Getter
    @Setter
    @NonNull
    private String permission;
    private final EnumMap<Language, Inventory> guis = new EnumMap<>(Language.class);

    public static ModularGUI fromName(@NonNull String internalName) {
        for (final ModularGUI gui : Database.guiList())
            if (gui.internalName.equalsIgnoreCase(internalName)) return gui;
        return null;
    }

    public void add(@NonNull Language language, @NonNull Inventory inventory) {
        if (!guis.containsKey(language)) this.guis.put(language, inventory);
        else Bukkit.getConsoleSender().sendMessage("");
    }

    public void addAll(@NonNull Map<Language, Inventory> inventories) {
        this.guis.putAll(inventories);
    }

    public void remove(@NonNull Language... languages) {
        for (int i = 0; i < languages.length; i++) {
            final Language language = languages[i];
            if (this.contains(language)) this.guis.remove(language);
        }
    }

    public boolean contains(@NonNull Language language) {
        return (this.get(language) != null);
    }

    public Inventory get(@NonNull Language language) {
        return (this.guis.containsKey(language) ? this.guis.get(language) : null);
    }

    public ConfigurationSection getSection() {
        return TownyWands.getParser().getConfig().getConfigurationSection("inventories").getConfigurationSection(this.internalName);
    }

    @Override
    public ModularGUI clone() {
        final ModularGUI gui = new ModularGUI(this.internalName, this.command, this.permission);
        gui.addAll(this.guis);
        return gui;
    }

}