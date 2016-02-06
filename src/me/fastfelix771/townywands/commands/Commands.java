package me.fastfelix771.townywands.commands;

import java.util.Arrays;
import java.util.List;
import me.fastfelix771.townywands.commands.CommandController.CommandHandler;
import me.fastfelix771.townywands.commands.CommandController.SubCommandHandler;
import me.fastfelix771.townywands.inventory.ModularGUI;
import me.fastfelix771.townywands.main.TownyWands;
import me.fastfelix771.townywands.utils.Invoker;
import me.fastfelix771.townywands.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands {

    private static final List<String> commands = Arrays.asList("§c/townywands §ahelp", "§c/townywands §a?", "§c/townywands §areload", "§c/gui §alist");

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

    @CommandHandler(
        name = "vsign",
        description = "Creates a virtual Sign and executes commands from the given input.",
        usage = "/vsign playername command {data}",
        permission = "townywands.cmd.vsign",
        permissionMessage = "§cYou are missing the permission §atownywands.cmd.vsign§c!")
    public void vsign(final CommandSender sender, final String[] args) {
        if ((args.length == 0) || (Bukkit.getPlayerExact(args[0]) == null)) return;

        // If not 1.8, do nothing.
        if (TownyWands.getSignGUI() == null) return;

        final Player player = Bukkit.getPlayerExact(args[0]);
        final StringBuilder sb = new StringBuilder();

        for (int i = 1; i < args.length; i++) {
            final String arg = args[i];
            sb.append(arg + " ");
        }

        TownyWands.getSignGUI().open(player, new Invoker<String[]>() {

            @Override
            public void invoke(final String[] lines) {
                final StringBuilder data = new StringBuilder();
                for (final String line : lines)
                    if (!line.isEmpty()) data.append(line);

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
        if ((args.length < 1) || (Bukkit.getPlayerExact(args[0]) == null)) return;

        final Player player = Bukkit.getPlayerExact(args[0]);
        final StringBuilder sb = new StringBuilder();

        for (int i = 1; i < args.length; i++) {
            final String arg = args[i];
            sb.append(arg + " ");
        }

        String fakeCommand = sb.toString();
        fakeCommand = fakeCommand.substring(0, fakeCommand.length() - 1);
        fakeCommand = "/" + fakeCommand;

        player.chat(fakeCommand);

    }

    @CommandHandler(
        name = "gui",
        description = "Creation and changing of GUIs",
        usage = "/gui help",
        permission = "townywands.cmd.gui",
        permissionMessage = "§cYou are missing the permission §atownywands.cmd.gui§c!")
    public void gui(final CommandSender sender, final String[] args) {
        /*
         * Ehh...im working on...complex idea sender.sendMessage("§6▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰"); sender.sendMessage("§2All available §b/gui §2commands: (Examples)"); sender.sendMessage("§b/gui create §3<§bname§3>"); sender.sendMessage("§b/gui set title"); sender.sendMessage("§b/gui set command"); sender.sendMessage("§b/gui set permission"); sender.sendMessage("§b/gui set slots"); sender.sendMessage("§b/gui delete §3<§bname§3>"); sender.sendMessage("§6▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰");
         */
        sender.sendMessage("Not yet implemented!");
    }

    @SubCommandHandler(
        name = "list",
        parent = "gui",
        permission = "townywands.cmd.gui.list",
        permissionMessage = "§cYou are missing the permission §atownywands.cmd.gui.list§c!")
    public void gui_list(final CommandSender sender, final String[] args) {
        sender.sendMessage("§6▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰");
        sender.sendMessage("§bTowny§3Wands §6- §aGUI's");

        for (int i = 0; i < TownyWands.getParser().getInventoryTokens().size(); i++) {
            final String token = TownyWands.getParser().getInventoryTokens().get(i);
            sender.sendMessage(new StringBuilder("§b§l ").append(token).append(" §6§l- §3 ").append("/").append(ModularGUI.fromName(token).getCommand()).toString());
        }

        sender.sendMessage("§6▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰");
    }

    @SubCommandHandler(
        name = "create",
        parent = "gui",
        permission = "townywands.cmd.gui.create",
        permissionMessage = "§cYou are missing the permission §atownywands.cmd.gui.create§c!")
    public void gui_create(final CommandSender sender, final String[] args) {
        /*
         * if (args.length == 0) { sender.sendMessage("§cYou need to specify an name for the gui!"); return; } final String name = args[0]; if (Mainclass.getParser().getInventoryTokens().contains(name)) { sender.sendMessage("§cA gui with this name already exists!"); return; } final Utf8YamlConfiguration config = Mainclass.getParser().getConfig(); final ConfigurationSection inv = config.getConfigurationSection("inventories").createSection(name); // Set some defaults here inv.set("command", "open-" + name); inv.set("permission", "townywands.gui." + name); inv.set("slots", 9);
         */
        sender.sendMessage("Not yet implemented!");
    }

}