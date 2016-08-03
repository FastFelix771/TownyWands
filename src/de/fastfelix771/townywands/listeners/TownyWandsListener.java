package de.fastfelix771.townywands.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.fastfelix771.townywands.api.GuiClickEvent;
import de.fastfelix771.townywands.inventory.ItemWrapper;
import de.fastfelix771.townywands.inventory.ModularGUI;
import de.fastfelix771.townywands.lang.Language;
import de.fastfelix771.townywands.main.TownyWands;
import de.fastfelix771.townywands.utils.Database;
import de.fastfelix771.townywands.utils.Updater.State;

public class TownyWandsListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();

        if(!TownyWands.isUpdateCheckingEnabled() || TownyWands.getUpdateResult() == null || TownyWands.getUpdateResult().getState() != State.UPDATE_FOUND) return;
        if ((p.isOp() || p.hasPermission("townywands.msg.update"))) {
            p.sendMessage("§4!UPDATE! §6-> TownyWands has found an update!");
            p.sendMessage("§4!UPDATE! §6-> You are currently on version §c" + TownyWands.getInstance().getDescription().getVersion());
            p.sendMessage("§4!UPDATE! §6-> Newest version is §c" + TownyWands.getUpdateResult().getLatestVersion());
/*
            if (Version.getCurrent().isOlderThan(Version.v1_8)) {
                p.sendMessage("§4!UPDATE! §6-> Download latest: §a" + TownyWands.getUpdateResult().getLatestURL());
                return;
            }

            if(Version.getCurrent().isNewerThan(Version.v1_7)) {
                if(Reflect.getClass("net.md_5.bungee.api.chat.TextComponent") == null) return;

                net.md_5.bungee.api.chat.TextComponent text = new net.md_5.bungee.api.chat.TextComponent("§4!UPDATE! §6-> Download latest: §a§l[Click Me]");
                text.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.OPEN_URL, TownyWands.getUpdateResult().getLatestURL()));
                p.spigot().sendMessage(text);
            }*/

        }
    }

    @EventHandler
    public void onCommand(final PlayerCommandPreprocessEvent e) { // add GuiOpenEvent & GuiCloseEvent
        final String command = e.getMessage().substring(1, e.getMessage().length());
        final Player p = e.getPlayer();

        if (!Database.contains(command)) return;
        e.setCancelled(true);

        Language lang = Language.getLanguage(p);
        ModularGUI gui = Database.get(command);

        final String permission = gui.getPermission();
        if (!p.hasPermission(permission)) {
            p.sendMessage("§cYou are missing the permission '§a" + permission + "§c'.");
            return;
        }

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

        if(inv != null) p.openInventory(inv);

    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();

        if ((item == null) || item.getType().equals(Material.AIR)) return;
        ItemWrapper wrapper = ItemWrapper.wrap(item);

        if (!(wrapper.hasValue("key") && Database.contains(wrapper.getValue("key", String.class)))) return;
        e.setCancelled(true);

        ItemWrapper eventWrapper = wrapper.clone(); // Prevents GUI modifications on accident, and allows for adding commands etc. on-the-fly.
        GuiClickEvent event = new GuiClickEvent(eventWrapper, p, Database.get(wrapper.getValue("key", String.class)), Database.get(wrapper.getValue("key", String.class), Language.getLanguage(p)));
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) return;

        onGuiClick(event);
    }

    // INTERNAL //

    public void onGuiClick(GuiClickEvent e) {

        Player p = e.getPlayer();
        // Add anti-spam click check maybe?

        String[] commands = e.getItemWrapper().getValue("commands");
        String[] console_commands = e.getItemWrapper().getValue("console_commands");

        if (commands != null && commands.length > 0) {
            for (String cmd : commands) {
                if (cmd.trim().isEmpty()) continue;

                cmd = cmd.replace("{playername}", p.getName());
                cmd = cmd.replace("{uuid}", p.getUniqueId().toString());
                cmd = cmd.replace("{world}", p.getWorld().getName());
                cmd = cmd.replace("{displayname}", p.getDisplayName());

                Bukkit.dispatchCommand(p, cmd);
            }
        }

        if (console_commands != null && console_commands.length > 0) {
            for (String cmd : console_commands) {
                if (cmd.trim().isEmpty()) continue;

                cmd = cmd.replace("{playername}", p.getName());
                cmd = cmd.replace("{uuid}", p.getUniqueId().toString());
                cmd = cmd.replace("{world}", p.getWorld().getName());
                cmd = cmd.replace("{displayname}", p.getDisplayName());

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            }
        }

    }

}