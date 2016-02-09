package de.fastfelix771.townywands.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Arrays;
import java.util.List;
import org.bukkit.entity.Player;
import de.fastfelix771.townywands.main.TownyWands;

public class Utils {

    private static final List<Integer> validCounts = Arrays.asList(9, 18, 27, 36, 45, 54);

    public static boolean isValidSlotCount(final int slots) {
        return validCounts.contains(slots);
    }

    public static void bungeeConnect(final Player player, final String servername) {
        if (TownyWands.isBungeecord()) {
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