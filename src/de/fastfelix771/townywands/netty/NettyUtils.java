package de.fastfelix771.townywands.netty;

import java.lang.reflect.Field;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import de.fastfelix771.townywands.utils.Invoker;
import de.fastfelix771.townywands.utils.Reflect;

public class NettyUtils {

    @SneakyThrows
    public static Object getChannel(@NonNull Player player) {
        final Object nms = Reflect.getNMSPlayer(player);
        final Field playerConnectionField = Reflect.getField(nms.getClass().getField("playerConnection"));
        final Object playerConnection = playerConnectionField.get(nms);
        final Field networkManagerField = Reflect.getField(playerConnection.getClass().getField("networkManager"));
        final Object networkManager = networkManagerField.get(playerConnection);

        for (final Field field : networkManager.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            switch (Reflect.getServerVersion()) {
                case UNKNOWN:
                    return null;
                case v1_7:
                    if (field.getType().isAssignableFrom(Reflect.getClass("net.minecraft.util.io.netty.channel.Channel"))) {
                        Object channel = field.get(networkManager);
                        return channel;
                    }
                    continue;
                case v1_8:
                    if (field.getType().isAssignableFrom(Reflect.getClass("io.netty.channel.Channel"))) {
                        Object channel = field.get(networkManager);
                        return channel;
                    }
                    continue;
                default:
                    return null;

            }
        }
        return null;
    }

    @SneakyThrows
    public static Object getPipeline(@NonNull Player player) {
        Object channel = getChannel(player);
        return Reflect.getMethod(channel.getClass().getMethod("pipeline")).invoke(channel);
    }

    @SneakyThrows
    public static void addHandler(@NonNull final String addAfter, @NonNull final String handlerName, @NonNull final Player target, @NonNull final Class<?> packetClass, @NonNull final Invoker<Object> invoker, final boolean dropPacket) {
        switch (Reflect.getServerVersion()) {
            case UNKNOWN:
                break;
            case v1_7:
                Object pipeline17 = getPipeline(target);
                Reflect.getMethod(pipeline17.getClass().getMethod("addAfter", String.class, String.class, Reflect.getClass("net.minecraft.util.io.netty.channel.ChannelHandler"))).invoke(pipeline17, addAfter, handlerName, new MessageToMessageDecoder_1_7(invoker, packetClass, dropPacket));
                break;
            case v1_8:
                Object pipeline18 = getPipeline(target);
                Reflect.getMethod(pipeline18.getClass().getMethod("addAfter", String.class, String.class, Reflect.getClass("io.netty.channel.ChannelHandler"))).invoke(pipeline18, addAfter, handlerName, new MessageToMessageDecoder_1_8(invoker, packetClass, dropPacket));
                break;
            default:
                break;
        }
    }

}