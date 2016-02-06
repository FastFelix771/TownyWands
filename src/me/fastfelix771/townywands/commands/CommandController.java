package me.fastfelix771.townywands.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author AmoebaMan
 * @changedBy FastFelix771
 */
public class CommandController implements CommandExecutor {

    private static final ConcurrentHashMap<Command, Object> handlers = new ConcurrentHashMap<Command, Object>();
    private static final ConcurrentHashMap<Command, Method> methods = new ConcurrentHashMap<Command, Method>();
    private static final ConcurrentHashMap<String, SubCommand> subCommands = new ConcurrentHashMap<String, SubCommand>();
    private static final ConcurrentHashMap<String, Object> subHandlers = new ConcurrentHashMap<String, Object>();
    private static final ConcurrentHashMap<String, Method> subMethods = new ConcurrentHashMap<String, Method>();

    public static void registerCommands(final JavaPlugin plugin, final Object handler) {

        for (final Method method : handler.getClass().getMethods()) {
            final Class<?>[] params = method.getParameterTypes();
            if (!((params.length == 2) && CommandSender.class.isAssignableFrom(params[0]) && String[].class.equals(params[1]))) return;

            if (isCommandHandler(method)) {
                final CommandHandler annotation = method.getAnnotation(CommandHandler.class);
                if (plugin.getCommand(annotation.name()) != null) {
                    plugin.getCommand(annotation.name()).setExecutor(new CommandController());
                    if (!(annotation.aliases().equals(new String[] { "" }))) plugin.getCommand(annotation.name()).setAliases(Arrays.asList(annotation.aliases()));
                    if (!annotation.description().equals("")) plugin.getCommand(annotation.name()).setDescription(annotation.description());
                    if (!annotation.usage().equals("")) plugin.getCommand(annotation.name()).setUsage(annotation.usage());
                    if (!annotation.permission().equals("")) plugin.getCommand(annotation.name()).setPermission(annotation.permission());
                    if (!annotation.permissionMessage().equals("")) plugin.getCommand(annotation.name()).setPermissionMessage(annotation.permissionMessage());
                    handlers.put(plugin.getCommand(annotation.name()), handler);
                    methods.put(plugin.getCommand(annotation.name()), method);
                }
            }

            if (isSubCommandHandler(method)) {
                final SubCommandHandler annotation = method.getAnnotation(SubCommandHandler.class);
                if (plugin.getCommand(annotation.parent()) != null) {
                    plugin.getCommand(annotation.parent()).setExecutor(new CommandController());
                    final SubCommand subCommand = new SubCommand(plugin.getCommand(annotation.parent()), annotation.name());
                    subCommand.permission = annotation.permission();
                    subCommand.permissionMessage = annotation.permissionMessage();
                    subCommands.put(subCommand.toString(), subCommand);
                    subHandlers.put(subCommand.toString(), handler);
                    subMethods.put(subCommand.toString(), method);
                }
            }
        }
    }

    private static boolean isCommandHandler(final Method method) {
        return method.getAnnotation(CommandHandler.class) != null;
    }

    private static boolean isSubCommandHandler(final Method method) {
        return method.getAnnotation(SubCommandHandler.class) != null;
    }

    @Retention(RetentionPolicy.RUNTIME)
    public static @interface CommandHandler {
        String name();

        String[] aliases() default { "" };

        String description() default "";

        String usage() default "";

        String permission() default "";

        String permissionMessage() default "§cYou do not have permission to use that command";
    }

    @Retention(RetentionPolicy.RUNTIME)
    public static @interface SubCommandHandler {
        String parent();

        String name();

        String permission() default "";

        String permissionMessage() default "§cYou do not have permission to use that command";
    }

    private static class SubCommand {
        public final Command superCommand;
        public final String subCommand;
        public String permission;
        public String permissionMessage;

        public SubCommand(final Command superCommand, final String subCommand) {
            this.superCommand = superCommand;
            this.subCommand = subCommand.toLowerCase();
        }

        @Override
        public boolean equals(final Object x) {
            return this.toString().equals(x.toString());
        }

        @Override
        public String toString() {
            return (this.superCommand.getName() + " " + this.subCommand).trim();
        }
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length > 0) {

            SubCommand subCommand = new SubCommand(command, args[0]);
            subCommand = subCommands.get(subCommand.toString());
            if (subCommand != null) {

                final Object subHandler = subHandlers.get(subCommand.toString());
                final Method subMethod = subMethods.get(subCommand.toString());
                if ((subHandler != null) && (subMethod != null)) {

                    final String[] subArgs = new String[args.length - 1];
                    for (int i = 1; i < args.length; i++)
                        subArgs[i - 1] = args[i];

                    if (subMethod.getParameterTypes()[0].equals(Player.class) && !(sender instanceof Player)) {
                        sender.sendMessage("§cThis command can only be used by players!");
                        return true;
                    }

                    if (subMethod.getParameterTypes()[0].equals(ConsoleCommandSender.class) && !(sender instanceof ConsoleCommandSender)) {
                        sender.sendMessage("§cThis command can only be used by the console!");
                        return true;
                    }

                    if (!subCommand.permission.isEmpty() && !sender.hasPermission(subCommand.permission)) {
                        sender.sendMessage(subCommand.permissionMessage);
                        return true;
                    }

                    try {
                        subMethod.invoke(subHandler, sender, args);
                    }
                    catch (final Exception e) {
                        sender.sendMessage("§cAn error occurred while trying to process the command");
                        e.printStackTrace();
                    }

                    return true;
                }
            }
        }

        final Object handler = handlers.get(command);
        final Method method = methods.get(command);
        if ((handler != null) && (method != null)) {

            if (method.getParameterTypes()[0].equals(Player.class) && !(sender instanceof Player)) {
                sender.sendMessage("§cThis command can only be used by players!");
                return true;
            }

            if (method.getParameterTypes()[0].equals(ConsoleCommandSender.class) && !(sender instanceof ConsoleCommandSender)) {
                sender.sendMessage("§cThis command can only be used by the console!");
                return true;
            }

            try {
                method.invoke(handler, sender, args);
            }
            catch (final Exception e) {
                sender.sendMessage("§cAn error occurred while trying to process the command");
                e.printStackTrace();
            }

            return true;
        }

        sender.sendMessage("Unknown command. Type \"help\" for help.");
        return true;
    }

}