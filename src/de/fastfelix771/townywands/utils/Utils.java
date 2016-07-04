package de.fastfelix771.townywands.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Arrays;
import java.util.List;
import org.bukkit.entity.Player;
import de.fastfelix771.townywands.main.TownyWands;

public class Utils {

    private static final List<Integer> validCounts = Arrays.asList(9, 18, 27, 36, 45, 54);

    public static boolean isValidSlotCount(int slots) {
        return validCounts.contains(slots);
    }

    public static void bungeeConnect(Player player, String servername) {
        if (TownyWands.isBungeecord()) {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            DataOutputStream dout = new DataOutputStream(bout);
            try {
                dout.writeUTF("Connect");
                dout.writeUTF(servername);
                player.sendPluginMessage(TownyWands.getInstance(), "BungeeCord", bout.toByteArray());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}