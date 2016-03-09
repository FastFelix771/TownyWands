package de.fastfelix771.townywands.commands;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import de.fastfelix771.townywands.commands.CommandController.CommandHandler;
import de.fastfelix771.townywands.commands.CommandController.SubCommandHandler;
import de.fastfelix771.townywands.main.Debug;
import de.fastfelix771.townywands.main.TownyWands;
import de.fastfelix771.townywands.utils.Invoker;
import de.fastfelix771.townywands.utils.Utils;

public class Commands {

    private static final List<String> commands = Arrays.asList("§c/townywands §ahelp", "§c/townywands §a?", "§c/townywands §areload");

    @CommandHandler(
        name = "townywands",
        description = "Provides basic features.",
        usage = "/townywands ?",
        permission = "townywands.cmd.townywands",
        permissionMessage = "§cYou are missing the permission §atownywands.cmd.townywands§c!",
        aliases = { "tws" })
    public void townywands(final CommandSender sender, final String[] args) {
        sender.sendMessage("§6▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰");
        sender.sendMessage("§bTowny§3Wands §6- §av§c" + TownyWands.getInstance().getDescription().getVersion());
        sender.sendMessage("§2Created by §6FastFelix771");
        sender.sendMessage("§cIf you need help, use §a/townywands help");
        sender.sendMessage("§6▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰");
    }

    @SubCommandHandler(
        name = "reload",
        parent = "townywands",
        permission = "townywands.cmd.reload",
        permissionMessage = "§cYou are missing the permission §atownywands.cmd.reload§c!")
    public void townywands_reload(final CommandSender sender, final String[] args) {
        TownyWands.reload();
        sender.sendMessage("§bTowny§3Wands §ahas been reloaded!");
    }

    @SubCommandHandler(
        name = "help",
        parent = "townywands",
        permission = "townywands.cmd.help",
        permissionMessage = "§cYou are missing the permission §atownywands.cmd.help§c!")
    public void townywands_help(final CommandSender sender, final String[] args) {
        sender.sendMessage("§6▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰");
        sender.sendMessage("§bTowny§3Wands §6- §aCommands");

        for (int i = 0; i < commands.size(); i++) {
            final StringBuilder sb = new StringBuilder();
            sb.append(commands.get(i));
            sender.sendMessage(sb.toString());
        }

        sender.sendMessage("§6▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰");
    }

    @SubCommandHandler(
        name = "?",
        parent = "townywands",
        permission = "townywands.cmd.help",
        permissionMessage = "§cYou are missing the permission §atownywands.cmd.help§c!")
    public void townywands_help2(final CommandSender sender, final String[] args) {
        this.townywands_help(sender, args);
    }

    @SubCommandHandler(
        name = "debug",
        parent = "townywands",
        permission = "townywands.cmd.debug",
        permissionMessage = "§cYou are missing the permission §atownywands.cmd.debug§c!")
    public void townywands_debug(final CommandSender sender, final String[] args) {
        if (sender instanceof Player) {
            if (Debug.players.contains(((Player) sender).getUniqueId())) Debug.players.remove(((Player) sender).getUniqueId());
            else Debug.players.add(((Player) sender).getUniqueId());
            sender.sendMessage(String.format("§6TownyWands DebugMode: §4%s", String.valueOf(Debug.players.contains(((Player) sender).getUniqueId())).toLowerCase()));
            return;
        }
        Debug.console = Debug.console ? false : true;
        sender.sendMessage(String.format("§6TownyWands DebugMode: §4%s", String.valueOf(Debug.console).toLowerCase()));

    }

    @CommandHandler(
        name = "vsign",
        description = "Creates a virtual Sign and executes commands from the given input.",
        usage = "/vsign playername command {data}",
        permission = "townywands.cmd.vsign",
        permissionMessage = "§cYou are missing the permission §atownywands.cmd.vsign§c!")
    public void vsign(final CommandSender sender, final String[] args) {
        if ((args.length == 0) || (Bukkit.getPlayerExact(args[0]) == null)) return;

        // If this version is not compatible, do nothing.
        if (TownyWands.getVirtualSign() == null) return;

        final Player player = Bukkit.getPlayerExact(args[0]);
        final StringBuilder sb = new StringBuilder();

        for (int i = 1; i < args.length; i++) {
            final String arg = args[i];
            sb.append(arg + " ");
        }

        TownyWands.getVirtualSign().show(player, new Invoker<String[]>() {

            @Override
            public void invoke(final String[] lines) {
                final StringBuilder data = new StringBuilder();
                for (final String line : lines)
                    if (line != null && !line.trim().isEmpty()) data.append(line);

                String command = sb.toString();
                command = command.substring(0, command.length() - 1);
                command = command.replace("{data}", data.toString());

                Bukkit.dispatchCommand(player, command); // Maybe add console commands here too?
            }
        });
    }

    @CommandHandler(
        name = "bungeetp",
        description = "Teleports a player to another server in your bungeecord network.",
        usage = "/bungeetp playername servername",
        permission = "townywands.cmd.bungeetp",
        permissionMessage = "§cYou are missing the permission §atownywands.cmd.bungeetp§c!")
    public void bungeetp(final CommandSender sender, final String[] args) {
        if (!(args.length == 2)) return;

        if (Bukkit.getPlayerExact(args[0]) == null) return;

        final Player player = Bukkit.getPlayerExact(args[0]);
        final String servername = args[1];

        try {
            Utils.bungeeConnect(player, servername); // If bungeecord is disabled the method will do nothing.
        }
        catch (final Exception e) {
            e.printStackTrace();
            sender.sendMessage("Failed to teleport " + player.getName() + " to server " + servername);
            return;
        }

    }

    @CommandHandler(
        name = "fakecmd",
        description = "Teleports a player to another server in your bungeecord network.",
        usage = "/fakecmd playername commandToExecute",
        permission = "townywands.cmd.fakecmd",
        permissionMessage = "§cYou are missing the permission §atownywands.cmd.fakecmd§c!")
    public void fakecmd(final CommandSender sender, final String[] args) {
        if ((args.length < 2) || (Bukkit.getPlayerExact(args[0]) == null)) return;

        final StringBuilder sb = new StringBuilder();

        for (int i = 1; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }

        Bukkit.getPlayerExact(args[0]).chat("/".concat(sb.toString()));

    }

    @CommandHandler(
        // TODO: register command to plugin.yml
        name = "modify",
        permission = "townywands.cmd.modify",
        permissionMessage = "§cYou are missing the permission §atownywands.cmd.modify§c!")
    public void modify(Player sender, String[] args) {
        // Command to control item behaivour with the new ItemWrapper.
        // TODO: Create a full-featured but simple /modify help menu.
        // TODO: Create a modify system via SubCommandHandlers.

    }

}