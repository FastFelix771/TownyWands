package me.fastfelix771.townywands.utils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class SignGUI implements Listener, Runnable {
	/**
	 * @author Janhektor
	 *         This class in licensed under GPLv3
	 *         For more information look at http://www.gnu.org/licenses/gpl-3.0
	 */
	// Thanks @Janhektor for this awesome class, i've modified it a "little bit" to allow better integration into TownyWands.

	private final JavaPlugin plugin;
	private final ConcurrentHashMap<UUID, Consumer<String[]>> inputResults;

	public SignGUI(final JavaPlugin plugin) {
		this.plugin = plugin;
		this.inputResults = new ConcurrentHashMap<UUID, Consumer<String[]>>();
		Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, this, 0L, 20 * 3L);

	}

	/**
	 * Use this method to read the SignInput from a player
	 * The accept()-method of your consumer will be called, when the player close the sign
	 * 
	 * @return boolean successful
	 * @param p
	 *            - The Player, who have to type an input
	 * @param result
	 *            - The consumer (String[]) for the result; String[] contains strings for 4 lines
	 */
	public boolean open(final Player p, final Consumer<String[]> result) {
		inputResults.put(p.getUniqueId(), result);
		try {
			final Class<?> packetClass = Reflect.getNMSClass("PacketPlayOutOpenSignEditor");
			final Class<?> blockPositionClass = Reflect.getNMSClass("BlockPosition");
			final Constructor<?> blockPosCon = blockPositionClass.getConstructor(new Class[] { int.class, int.class, int.class });
			final Object blockPosition = blockPosCon.newInstance(new Object[] { 0, 0, 0 });
			final Constructor<?> packetCon = packetClass.getConstructor(new Class[] { blockPositionClass });
			final Object packet = packetCon.newInstance(new Object[] { blockPosition });
			Reflect.sendPacket(p, packet);
			return true;
		} catch (final Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/* Garbage Collection */
	@Override
	public void run() {
		for (final UUID uuid : inputResults.keySet()) {
			if (Bukkit.getPlayer(uuid) == null)
				inputResults.remove(uuid);
		}
	}

	/* Events */
	@EventHandler
	public void onJoin(final PlayerJoinEvent e) {
		final Player p = e.getPlayer();
		NettyUtils.getPipeline(p).addAfter("decoder", "TownyWands_vSigns", new MessageToMessageDecoder<Object>() {
			@Override
			protected void decode(final ChannelHandlerContext chc, final Object packet, final List<Object> packetList) throws Exception {
				if (Reflect.getNMSClass("PacketPlayInUpdateSign").isInstance(packet)) {
					final Method bMethod = packet.getClass().getMethod("b");
					final Object chatBaseComponents = bMethod.invoke(packet);
					final String[] lines = new String[4];
					for (int i = 0; i < 4; i++) {
						final Object chatComponent = Array.get(chatBaseComponents, i);
						final Method getText = chatComponent.getClass().getMethod("getText");
						lines[i] = (String) getText.invoke(chatComponent);
					}
					if (inputResults.containsKey(p.getUniqueId())) {
						inputResults.get(p.getUniqueId()).accept(lines);
						inputResults.remove(p.getUniqueId());
					}
				}
				packetList.add(packet);
			}
		});
	}

}