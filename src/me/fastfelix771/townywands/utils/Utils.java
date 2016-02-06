package me.fastfelix771.townywands.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import me.fastfelix771.townywands.main.TownyWands;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

public class Utils {

    private static final Integer[] validCounts = new Integer[] { 9, 18, 27, 36, 45, 54 };

    /*
     * Maybe ill need this snippet sometime... public static Resident getResident(final Player player) { final String name = player.getName(); Resident res; try { res = TownyUniverse.getDataSource().getResident(name); } catch (final NotRegisteredException e) { res = new Resident(name); TownyUniverse.getDataSource().saveResident(res); } return res; }
     */

    public static boolean isValidSlotCount(final int slots) {
        return Arrays.asList(validCounts).contains(slots);
    }

    public enum Type {
        CONSOLE, PLAYER;
    }

    public static ItemStack setCommands(ItemStack item, final List<String> commands, final Type type) {
        try {
            final Method asNMSCopy = Reflect.getMethod(Reflect.CraftItemStack.getDeclaredMethod("asNMSCopy", ItemStack.class));
            final Object nms = asNMSCopy.invoke(Reflect.CraftItemStack, item);
            final Constructor<?> NBTTagCompound = Reflect.getConstructor(Reflect.NBTTagCompound);
            final Method hasTag = Reflect.getMethod(Reflect.ItemStack.getDeclaredMethod("hasTag"));
            final Method getTag = Reflect.getMethod(Reflect.ItemStack.getDeclaredMethod("getTag"));
            final Method setTag = Reflect.getMethod(Reflect.ItemStack.getDeclaredMethod("setTag", Reflect.NBTTagCompound));
            final Object tag = ((boolean) hasTag.invoke(nms) ? getTag.invoke(nms) : NBTTagCompound.newInstance());
            final Method setString = Reflect.getMethod(Reflect.NBTTagCompound.getDeclaredMethod("setString", String.class, String.class));
            final Method asCraftMirror = Reflect.getMethod(Reflect.CraftItemStack.getDeclaredMethod("asCraftMirror", Reflect.ItemStack));

            final String json = JSONArray.toJSONString(commands); // Needed to switch from Gson to plain JSON to be backwards-compatible to 1.6.4

            if (type == Type.PLAYER) setString.invoke(tag, "townywands_commands", json);
            else if (type == Type.CONSOLE) setString.invoke(tag, "townywands_console_commands", json);

            if (!(boolean) hasTag.invoke(nms)) setTag.invoke(nms, tag);

            item = (ItemStack) asCraftMirror.invoke(null, nms);
            return item;
        }
        catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static List<String> getCommands(final ItemStack item, final Type type) {
        try {
            final Method asNMSCopy = Reflect.getMethod(Reflect.CraftItemStack.getDeclaredMethod("asNMSCopy", ItemStack.class));
            final Object nms = asNMSCopy.invoke(Reflect.CraftItemStack, item);
            final Constructor<?> NBTTagCompound = Reflect.getConstructor(Reflect.NBTTagCompound);
            final Method hasTag = Reflect.getMethod(Reflect.ItemStack.getDeclaredMethod("hasTag"));
            final Method getTag = Reflect.getMethod(Reflect.ItemStack.getDeclaredMethod("getTag"));
            final Object tag = ((boolean) hasTag.invoke(nms) ? getTag.invoke(nms) : NBTTagCompound.newInstance());
            final Method getString = Reflect.getMethod(Reflect.NBTTagCompound.getDeclaredMethod("getString", String.class));

            // Needed to switch from Gson to plain JSON to be backwards-compatible to 1.6.4
            if (type == Type.PLAYER) if (getString.invoke(tag, "townywands_commands") != null) {
                final List<String> commands = (List<String>) JSONValue.parse((String) getString.invoke(tag, "townywands_commands"));
                return commands;
            }

            if (type == Type.CONSOLE) if (getString.invoke(tag, "townywands_console_commands") != null) {
                final List<String> commands = (List<String>) JSONValue.parse((String) getString.invoke(tag, "townywands_console_commands"));
                return commands;
            }

            return null;
        }
        catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ItemStack setKey(ItemStack item, final String key) {
        try {
            final Method asNMSCopy = Reflect.getMethod(Reflect.CraftItemStack.getDeclaredMethod("asNMSCopy", ItemStack.class));
            final Object nms = asNMSCopy.invoke(Reflect.CraftItemStack, item);
            final Constructor<?> NBTTagCompound = Reflect.getConstructor(Reflect.NBTTagCompound);
            final Method hasTag = Reflect.getMethod(Reflect.ItemStack.getDeclaredMethod("hasTag"));
            final Method getTag = Reflect.getMethod(Reflect.ItemStack.getDeclaredMethod("getTag"));
            final Method setTag = Reflect.getMethod(Reflect.ItemStack.getDeclaredMethod("setTag", Reflect.NBTTagCompound));
            final Object tag = ((boolean) hasTag.invoke(nms) ? getTag.invoke(nms) : NBTTagCompound.newInstance());
            final Method setString = Reflect.getMethod(Reflect.NBTTagCompound.getDeclaredMethod("setString", String.class, String.class));
            final Method asCraftMirror = Reflect.getMethod(Reflect.CraftItemStack.getDeclaredMethod("asCraftMirror", Reflect.ItemStack));

            setString.invoke(tag, "townywands_key", key);

            if (!(boolean) hasTag.invoke(nms)) setTag.invoke(nms, tag);

            item = (ItemStack) asCraftMirror.invoke(null, nms);
            return item;
        }
        catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getKey(final ItemStack item) {
        try {
            final Method asNMSCopy = Reflect.getMethod(Reflect.CraftItemStack.getDeclaredMethod("asNMSCopy", ItemStack.class));
            final Object nms = asNMSCopy.invoke(Reflect.CraftItemStack, item);
            final Constructor<?> NBTTagCompound = Reflect.getConstructor(Reflect.NBTTagCompound);
            final Method hasTag = Reflect.getMethod(Reflect.ItemStack.getDeclaredMethod("hasTag"));
            final Method getTag = Reflect.getMethod(Reflect.ItemStack.getDeclaredMethod("getTag"));
            final Object tag = ((boolean) hasTag.invoke(nms) ? getTag.invoke(nms) : NBTTagCompound.newInstance());
            final Method getString = Reflect.getMethod(Reflect.NBTTagCompound.getDeclaredMethod("getString", String.class));

            final String key = (String) getString.invoke(tag, "townywands_key");

            // If the item has no key it will return an empty string...that would block 90% of all InventoryClickEvents, so check for it and return null instead.
            if (key.isEmpty() || (key == "") || (key == null)) return null;

            return key;
        }
        catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ItemStack addEnchantmentGlow(ItemStack item) {
        try {
            final Method asNMSCopy = Reflect.getMethod(Reflect.CraftItemStack.getDeclaredMethod("asNMSCopy", ItemStack.class));
            final Object nms = asNMSCopy.invoke(Reflect.CraftItemStack, item);
            final Constructor<?> NBTTagCompound = Reflect.getConstructor(Reflect.NBTTagCompound);
            final Method hasTag = Reflect.getMethod(Reflect.ItemStack.getDeclaredMethod("hasTag"));
            final Method getTag = Reflect.getMethod(Reflect.ItemStack.getDeclaredMethod("getTag"));
            final Method setTag = Reflect.getMethod(Reflect.ItemStack.getDeclaredMethod("setTag", Reflect.NBTTagCompound));
            final Object tag = ((boolean) hasTag.invoke(nms) ? getTag.invoke(nms) : NBTTagCompound.newInstance());
            final Method setString = Reflect.getMethod(Reflect.NBTTagCompound.getDeclaredMethod("setString", String.class, String.class));
            final Method asCraftMirror = Reflect.getMethod(Reflect.CraftItemStack.getDeclaredMethod("asCraftMirror", Reflect.ItemStack));

            final String enchantments = Reflect.getConstructor(Reflect.getNMSClass("NBTTagList")).newInstance().toString();
            setString.invoke(tag, "ench", enchantments);

            if (!(boolean) hasTag.invoke(nms)) setTag.invoke(nms, tag);

            item = (ItemStack) asCraftMirror.invoke(null, nms);
            return item;
        }
        catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // I need to do this on the NBT way to be backwards-compatible to 1.7 and lower.
    public static ItemStack hideFlags(ItemStack item) {
        try {
            final Method asNMSCopy = Reflect.getMethod(Reflect.CraftItemStack.getDeclaredMethod("asNMSCopy", ItemStack.class));
            final Object nms = asNMSCopy.invoke(Reflect.CraftItemStack, item);
            final Constructor<?> NBTTagCompound = Reflect.getConstructor(Reflect.NBTTagCompound);
            final Method hasTag = Reflect.getMethod(Reflect.ItemStack.getDeclaredMethod("hasTag"));
            final Method getTag = Reflect.getMethod(Reflect.ItemStack.getDeclaredMethod("getTag"));
            final Method setTag = Reflect.getMethod(Reflect.ItemStack.getDeclaredMethod("setTag", Reflect.NBTTagCompound));
            final Object tag = ((boolean) hasTag.invoke(nms) ? getTag.invoke(nms) : NBTTagCompound.newInstance());
            final Method setString = Reflect.getMethod(Reflect.NBTTagCompound.getDeclaredMethod("setString", String.class, String.class));
            final Method asCraftMirror = Reflect.getMethod(Reflect.CraftItemStack.getDeclaredMethod("asCraftMirror", Reflect.ItemStack));

            setString.invoke(tag, "HideFlags", "1");

            if (!(boolean) hasTag.invoke(nms)) setTag.invoke(nms, tag);

            item = (ItemStack) asCraftMirror.invoke(null, nms);
            return item;
        }
        catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void bungeeConnect(final Player player, final String servername) {
        if (TownyWands.getInstance().getBungeecord()) {
            final ByteArrayOutputStream bout = new ByteArrayOutputStream();
            final DataOutputStream dout = new DataOutputStream(bout);
            try {
                dout.writeUTF("Connect");
                dout.writeUTF(servername);
                player.sendPluginMessage(TownyWands.getInstance(), "BungeeCord", bout.toByteArray());
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

}