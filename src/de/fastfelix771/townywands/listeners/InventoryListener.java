package de.fastfelix771.townywands.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import de.fastfelix771.townywands.inventory.ItemWrapper;
import de.fastfelix771.townywands.inventory.ModularGUI;
import de.fastfelix771.townywands.lang.Language;
import de.fastfelix771.townywands.utils.Database;

public class InventoryListener implements Listener {

    @EventHandler
    public void onCommand(final PlayerCommandPreprocessEvent e) {
        final String command = e.getMessage().substring(1, e.getMessage().length());
        final Player p = e.getPlayer();

        if (!Database.contains(command)) return;
        e.setCancelled(true);

        final Language lang = Language.getLanguage(p);
        final ModularGUI gui = Database.get(command);
        Inventory inv = null;

        if (gui.contains(lang)) inv = gui.get(lang);
        else {
            if (!gui.contains(Language.ENGLISH)) {
                p.sendMessage("§cTownyWands | §aThere is no GUI registered in your language nor the default one (§6ENGLISH§a)!");
                p.sendMessage("§cPlease report this to an administrator!");
                return;
            }
            inv = gui.get(Language.ENGLISH);
        }

        final String permission = gui.getPermission();
        if (!p.hasPermission(permission)) {
            p.sendMessage("§cYou are missing the permission '§a" + permission + "§c'.");
            return;
        }

        p.openInventory(inv);

    }

    @EventHandler
    public void onInvClick(final InventoryClickEvent e) {
        final Player p = (Player) e.getWhoClicked();
        final ItemStack item = e.getCurrentItem();

        if ((item == null) || item.getType().equals(Material.AIR)) return;
        ItemWrapper wrapper = ItemWrapper.wrap(item);

        if (!wrapper.hasValue("key")) return;
        e.setCancelled(true);

        final String command = wrapper.getValue("key");

        if (!Database.contains(command)) return;

        String[] commands = wrapper.getValue("commands");
        String[] console_commands = wrapper.getValue("console_commands");

        if (commands != null && commands.length > 0) {
            for (String cmd : commands) {
                if (cmd.replace(" ", "").isEmpty()) continue;

                cmd = cmd.replace("{playername}", p.getName());
                cmd = cmd.replace("{uuid}", p.getUniqueId().toString());
                cmd = cmd.replace("{world}", p.getWorld().getName());
                cmd = cmd.replace("{displayname}", p.getDisplayName());

                Bukkit.dispatchCommand(p, cmd);
            }
        }

        if (console_commands != null && console_commands.length > 0) {
            for (String cmd : console_commands) {
                if (cmd.replace(" ", "").isEmpty()) continue;

                cmd = cmd.replace("{playername}", p.getName());
                cmd = cmd.replace("{uuid}", p.getUniqueId().toString());
                cmd = cmd.replace("{world}", p.getWorld().getName());
                cmd = cmd.replace("{displayname}", p.getDisplayName());

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            }
        }

    }

}