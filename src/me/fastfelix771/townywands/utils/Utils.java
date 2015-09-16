package me.fastfelix771.townywands.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.fastfelix771.townywands.lang.Language;
import net.minecraft.server.v1_8_R1.NBTTagCompound;
import net.minecraft.server.v1_8_R1.NBTTagList;

import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class Utils {

	private static final Integer[] validCounts = new Integer[] { 9, 18, 27, 36, 45, 54 };

	/**
	 * @param player
	 *            the player object you want to turn into a towny resident
	 * @return a towny resident of the given player
	 */
	public static Resident getResident(final Player player) {
		final String name = player.getName();
		Resident res;
		try {
			res = TownyUniverse.getDataSource().getResident(name);
		} catch (final NotRegisteredException e) {
			res = new Resident(name);
			TownyUniverse.getDataSource().saveResident(res);
		}
		return res;
	}

	public static boolean isValidSlotCount(final int slots) {
		return Arrays.asList(validCounts).contains(slots);
	}

	public static ItemStack setCommands(ItemStack item, final List<String> commands, final Language language) {
		final net.minecraft.server.v1_8_R1.ItemStack nms = CraftItemStack.asNMSCopy(item);
		final NBTTagCompound tag = nms.hasTag() ? nms.getTag() : new NBTTagCompound();

		final Gson gson = new Gson();
		final String json = gson.toJson(commands);

		tag.setString("townywands_commands_" + language.getCode(), json);

		item = CraftItemStack.asCraftMirror(nms);
		return item;
	}

	@SuppressWarnings("unchecked")
	public static List<String> getCommands(final ItemStack item, final Language language) {
		final net.minecraft.server.v1_8_R1.ItemStack nms = CraftItemStack.asNMSCopy(item);
		final NBTTagCompound tag = nms.hasTag() ? nms.getTag() : new NBTTagCompound();

		if (tag.getString("townywands_commands_" + language.getCode()) != null) {
			final Gson gson = new Gson();
			final List<String> commands = gson.fromJson(tag.getString("townywands_commands_" + language.getCode()), ArrayList.class);
			return commands;
		}

		return null;
	}

	public static ItemStack setKey(ItemStack item, final String key) {
		final net.minecraft.server.v1_8_R1.ItemStack nms = CraftItemStack.asNMSCopy(item);
		final NBTTagCompound tag = nms.hasTag() ? nms.getTag() : new NBTTagCompound();

		tag.setString("townywands_key", key);

		item = CraftItemStack.asCraftMirror(nms);
		return item;
	}

	public static String getKey(final ItemStack item) {
		final net.minecraft.server.v1_8_R1.ItemStack nms = CraftItemStack.asNMSCopy(item);
		final NBTTagCompound tag = nms.hasTag() ? nms.getTag() : new NBTTagCompound();

		if (tag.getString("townywands_key") != null) {
			return tag.getString("townywands_key");
		}

		return null;
	}

	public static ItemStack addEnchantmentGlow(ItemStack item) {
		final net.minecraft.server.v1_8_R1.ItemStack nms = CraftItemStack.asNMSCopy(item);
		final NBTTagCompound tag = nms.hasTag() ? nms.getTag() : new NBTTagCompound();

		final String enchantments = new NBTTagList().toString();
		tag.setString("ench", enchantments);

		item = CraftItemStack.asCraftMirror(nms);
		return item;
	}

}