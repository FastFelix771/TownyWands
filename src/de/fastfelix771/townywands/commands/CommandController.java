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
package de.fastfelix771.townywands.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

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

    private static final HashMap<Command, Object> handlers = new HashMap<Command, Object>();
    private static final HashMap<Command, Method> methods = new HashMap<Command, Method>();
    private static final HashMap<String, SubCommand> subCommands = new HashMap<String, SubCommand>();
    private static final HashMap<String, Object> subHandlers = new HashMap<String, Object>();
    private static final HashMap<String, Method> subMethods = new HashMap<String, Method>();

    public static void registerCommands(JavaPlugin plugin, Object handler) {

        for (Method method : handler.getClass().getMethods()) {
            Class<?>[] params = method.getParameterTypes();
            if (!((params.length == 2) && CommandSender.class.isAssignableFrom(params[0]) && String[].class.equals(params[1]))) return;

            if (isCommandHandler(method)) {
                CommandHandler annotation = method.getAnnotation(CommandHandler.class);
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
                SubCommandHandler annotation = method.getAnnotation(SubCommandHandler.class);
                if (plugin.getCommand(annotation.parent()) != null) {
                    plugin.getCommand(annotation.parent()).setExecutor(new CommandController());
                    SubCommand subCommand = new SubCommand(plugin.getCommand(annotation.parent()), annotation.name());
                    subCommand.permission = annotation.permission();
                    subCommand.permissionMessage = annotation.permissionMessage();
                    subCommands.put(subCommand.toString(), subCommand);
                    subHandlers.put(subCommand.toString(), handler);
                    subMethods.put(subCommand.toString(), method);
                }
            }
        }
    }

    private static boolean isCommandHandler(Method method) {
        return method.getAnnotation(CommandHandler.class) != null;
    }

    private static boolean isSubCommandHandler(Method method) {
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
        public Command superCommand;
        public String subCommand;
        public String permission;
        public String permissionMessage;

        public SubCommand(Command superCommand, String subCommand) {
            this.superCommand = superCommand;
            this.subCommand = subCommand.toLowerCase();
        }

        @Override
        public boolean equals(Object x) {
            return this.toString().equals(x.toString());
        }

        @Override
        public String toString() {
            return (this.superCommand.getName() + " " + this.subCommand).trim();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {

            SubCommand subCommand = new SubCommand(command, args[0]);
            subCommand = subCommands.get(subCommand.toString());
            if (subCommand != null) {

                Object subHandler = subHandlers.get(subCommand.toString());
                Method subMethod = subMethods.get(subCommand.toString());
                if ((subHandler != null) && (subMethod != null)) {

                    String[] subArgs = new String[args.length - 1];
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
                    catch (Exception e) {
                        sender.sendMessage("§cAn error occurred while trying to process the command");
                        e.printStackTrace();
                    }

                    return true;
                }
            }
        }

        Object handler = handlers.get(command);
        Method method = methods.get(command);
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
            catch (Exception e) {
                sender.sendMessage("§cAn error occurred while trying to process the command");
                e.printStackTrace();
            }

            return true;
        }

        sender.sendMessage("Unknown command. Type \"help\" for help.");
        return true;
    }

}
