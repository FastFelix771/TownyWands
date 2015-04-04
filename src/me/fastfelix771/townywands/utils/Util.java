package me.fastfelix771.townywands.utils;

import java.io.File;
import java.util.ArrayList;

import me.fastfelix771.townywands.main.Mainclass;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.spigotmc.ProtocolInjector.PacketTitle.Action;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public final class Util {

	private static Util instance;

	private final File msgfile = new File(Mainclass.getInstance().getDataFolder().getAbsolutePath() + "/messages.yml");
	private final YamlConfiguration messages = YamlConfiguration.loadConfiguration(msgfile);

	/**
	 * @author FastFelix771
	 * @param title
	 *            sets the title you want to send
	 * @param subtitle
	 *            sets the subtitle you want to send
	 * @param staytime
	 *            How much seconds the player should see the titles?
	 * @param fadein
	 *            how much seconds should it take to fade in the title?
	 * @param fadeout
	 *            how much seconds should it take to fade out the title?
	 * @param player
	 *            is just the player who sees the title when its done :3
	 */
	public static final void push(final String title, final String subtitle, final int staytime, final int fadein, final int fadeout, final Player player) {
		final Title tit = new Title(colorize(title), Action.TITLE, fadein, staytime, fadeout);
		final Title sub = new Title(colorize(subtitle), Action.SUBTITLE, fadein, staytime, fadeout);
		tit.build();
		sub.build();
		tit.send(player);
		sub.send(player);
		// Isnt that a nice messaging system? I <3 titles^^
	}

	/**
	 * @author FastFelix771
	 * @param items
	 * @return an ItemStack array from the input list...i only made this method bcause im lazy ^^
	 */
	public static final ItemStack[] list2array(final ArrayList<ItemStack> items) {
		final ItemStack[] itemlist = items.toArray(new ItemStack[items.size()]);
		return itemlist;
	}

	/**
	 * @author FastFelix771
	 * @param name
	 *            sets the displayname of the item
	 * @param lore
	 *            sets the "subtitles" for the item
	 * @param quantity
	 *            sets the quantity of the item like 3
	 * @return an item stack built from the input informations, very useful in my opinion...
	 */
	// Feel free to use this method for your own GUIs :)
	public static final ItemStack createItem(final String name, final ArrayList<String> lore, final int quantity, final Material material) {
		final ItemStack item = new ItemStack(material, quantity);
		final ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	/**
	 * @author FastFelix771
	 * @param name
	 *            sets the displayname of the item
	 * @param lore
	 *            sets a single subname for the item, saves space if you only need one lore
	 * @param quantity
	 *            sets the quantity of the item like 3
	 * @return an item stack built from the input informations, very useful in my opinion...
	 */
	// Feel free to use this method for your own GUIs :)
	public static final ItemStack createItem(final String name, final String lore, final int quantity, final Material material) {
		final ItemStack item = new ItemStack(material, quantity);
		final ArrayList<String> lorelist = new ArrayList<String>();
		lorelist.add(lore);
		final ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lorelist);
		item.setItemMeta(meta);
		return item;
	}

	/**
	 * @author FastFelix771
	 * @param player
	 * @return an resident from the player, to work with towny :)
	 */
	public static final Resident getResident(final Player player) {
		try {
			final Resident res = TownyUniverse.getDataSource().getResident(player.getName());
			return res;
		} catch (final NotRegisteredException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @author FastFelix771
	 * @param message
	 * @return The input message but now with colors! Great for config messages!
	 */
	public static final String colorize(final String message) {
		final String colo = message.replaceAll("&([0-9a-fA-Fk-pK-PrR])", "§$1");
		return colo;
	}

	/**
	 * @author FastFelix771
	 * @param message
	 * @param target
	 * @param replacement
	 * @return the input string with your wished replacements, good for config messages too!
	 */
	public static final String replace(final String message, final String target, final String replacement) {
		final String rplcd = message.replace(target, replacement);
		return rplcd;
	}

	/**
	 * @author FastFelix771
	 * @param player
	 *            receives the No Permission error from the config!
	 */
	public static final void sendPermError(final Player player) {
		player.sendMessage(colorize(getInstance().messages.getString("noPermissions")));
	}

	// Dirty trick to get my messagefile ._.
	private static final Util getInstance() {
		return instance;
	}
}