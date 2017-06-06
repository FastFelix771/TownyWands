/*******************************************************************************
 * Copyright (C) 2017 Felix Drescher-Hackel
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package de.fastfelix771.townywands.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import de.fastfelix771.townywands.api.events.GuiClickEvent;
import de.fastfelix771.townywands.files.InventoryCommand;
import de.fastfelix771.townywands.main.TownyWands;
import de.fastfelix771.townywands.utils.Reflect;
import de.fastfelix771.townywands.utils.Updater.State;
import de.unitygaming.bukkit.vsign.Version;

public class TownyWandsListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {

		Player p = e.getPlayer();

		if(!TownyWands.getConfiguration().updateChecking || TownyWands.getUpdateResult() == null || TownyWands.getUpdateResult().getState() != State.UPDATE_FOUND) return;
		if ((p.isOp() || p.hasPermission("townywands.msg.update"))) {
			p.sendMessage("§4!UPDATE! §6-> TownyWands has found an update!");
			p.sendMessage("§4!UPDATE! §6-> You are currently on version §c" + TownyWands.getInstance().getDescription().getVersion());
			p.sendMessage("§4!UPDATE! §6-> Newest version is §c" + TownyWands.getUpdateResult().getLatestVersion());

			if (Version.getCurrent().isOlderThan(Version.v1_8)) {
				p.sendMessage("§4!UPDATE! §6-> Download latest: §a" + TownyWands.getUpdateResult().getLatestURL());
				return;
			}

			if(Version.getCurrent().isNewerThan(Version.v1_7)) {
				if(Reflect.getClass("net.md_5.bungee.api.chat.TextComponent") == null || Reflect.getClass("net.md_5.bungee.api.chat.BaseComponent") == null) return;

				net.md_5.bungee.api.chat.TextComponent text = new net.md_5.bungee.api.chat.TextComponent("§4!UPDATE! §6-> Download latest: §a§l[Click Me]");
				text.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.OPEN_URL, TownyWands.getUpdateResult().getLatestURL()));
				p.spigot().sendMessage(text);
			}

		}
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		if(e.getMessage().trim().isEmpty() || e.getMessage().trim().length() < 2) return;

		String command = e.getMessage().substring(1, e.getMessage().length());
		Player p = e.getPlayer();


	}

	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		//		Player p = (Player) e.getWhoClicked();
		//		ItemStack item = e.getCurrentItem();
		//
		//		if ((item == null) || item.getType().equals(Material.AIR)) return;
		//		ItemWrapper wrapper = ItemWrapper.wrap(item);
		//
		//		if(!wrapper.hasNBTKey("townywands_id")) return;
		//		e.setCancelled(true);
		//
		//		GuiClickEvent event = new GuiClickEvent(p, ModularItem.fromID(wrapper.getNBTKey("townywands_id", int.class)));
		//		Bukkit.getPluginManager().callEvent(event);
		//		if(event.isCancelled()) return;
		//
		//		onGuiClick(event);
	}

	private void onGuiClick(GuiClickEvent e) {
		Player p = e.getPlayer();

		for (InventoryCommand command : e.getItem().getCommands()) {
			if (command.getValue().trim().isEmpty()) continue;

			String cmd = command.getValue();

			cmd = cmd.replace("{playername}", p.getName());
			cmd = cmd.replace("{uuid}", p.getUniqueId().toString());
			cmd = cmd.replace("{world}", p.getWorld().getName());
			cmd = cmd.replace("{displayname}", p.getDisplayName());

			Bukkit.dispatchCommand(command.isConsole() ? Bukkit.getConsoleSender() : p, cmd);
		}

	}

}
