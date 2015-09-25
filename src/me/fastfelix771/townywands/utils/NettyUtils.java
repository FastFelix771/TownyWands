package me.fastfelix771.townywands.utils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;

import java.lang.reflect.Field;

import org.bukkit.entity.Player;

public class NettyUtils {

	public static Channel getChannel(final Player player) {
		try {
			final Object nms = Reflect.getNMSPlayer(player);
			final Field playerConnectionField = Reflect.getField(nms.getClass().getField("playerConnection"));
			final Object playerConnection = playerConnectionField.get(nms);
			final Field networkManagerField = Reflect.getField(playerConnection.getClass().getField("networkManager"));
			final Object networkManager = networkManagerField.get(playerConnection);
			Channel channel = null;

			for (final Field field : networkManager.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				if (field.getType().isAssignableFrom(Channel.class)) {
					channel = (Channel) field.get(networkManager);
					break;
				}
			}

			return channel;
		} catch (final Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static ChannelPipeline getPipeline(final Player player) {
		return getChannel(player).pipeline();
	}

}