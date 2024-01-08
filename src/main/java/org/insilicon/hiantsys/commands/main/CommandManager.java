package org.insilicon.hiantsys.commands.main;

import net.cybercake.cyberapi.spigot.basic.BetterStackTraces;
import net.cybercake.cyberapi.spigot.chat.UChat;
import net.cybercake.cyberapi.spigot.chat.UTabComp;
import net.cybercake.cyberapi.spigot.chat.centered.CenteredMessage;
import net.cybercake.cyberapi.spigot.server.commands.CommandInformation;
import net.cybercake.cyberapi.spigot.server.commands.SpigotCommand;
import net.md_5.bungee.api.chat.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.insilicon.hiantsys.Utils.HiantUtils;
import org.insilicon.hiantsys.commands.main.subcommands.Help;
import org.insilicon.hiantsys.commands.main.subcommands.Reload;
import org.insilicon.hiantsys.commands.main.subcommands.Tasks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"unused", "deprecation"})
public class CommandManager extends SpigotCommand {

    private final static String pluginPermission = "";
    private final static String pluginName = "Hiant";
    private final static String pluginTitle = "Hiant";
    private final static String noPermissionMsg = "&cYou don't have permission to use this!";
    private final ArrayList<SubCommand> subcommands = new ArrayList<>();

    public CommandManager() {
        super(
                newCommand("hiant")
                        .setDescription("The main command for Hiant's java plugin")
                        .setUsage("/hiant [subcomand]")
                        .setAliases("h")
                        .setPermission(pluginPermission, noPermissionMsg)
                        .build()
        );
        subcommands.add(new Help());
        subcommands.add(new Reload());
        subcommands.add(new Tasks());
    }

    @SuppressWarnings("deprecation")
    public static void printHelpMsgSpecific(CommandSender sender, String description, String usage, String permission, String aliases) {
        if (permission.equalsIgnoreCase("")) {
            permission = "Everyone";
        }

        String ifAliases = "";
        if (!aliases.equalsIgnoreCase("[]")) {
            ifAliases = "\n&6Aliases: &f" + aliases;
        }
        BaseComponent component = new TextComponent(UChat.chat("&9/" + (pluginName.toLowerCase()) + " " + usage));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, usage));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(UChat.chat("&6Command: &f" + usage + "\n&6Description: &f" + description + "\n&6Permission: &f" + permission + ifAliases)).create()));
        sender.sendMessage(component);
    }

    @Override
    public boolean perform(@NotNull CommandSender sender, @NotNull String command, CommandInformation information, String[] args) {
        if (getSubCommandsOnlyWithPerms(sender).size() <= 1) {
            sender.sendMessage(HiantUtils.coloredMiniMessage(noPermissionMsg));
        } else if (args.length == 0) {
            printHelpMsg(sender);
        } else {
            boolean ran = false;
            if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("info")) {
                printHelpMsg(sender);
            } else {
                for (SubCommand cmd : getSubcommands()) {
                    boolean use = args[0].equalsIgnoreCase(cmd.getName());
                    if (!use) {
                        for (String alias : cmd.getAliases()) {
                            if (args[0].equalsIgnoreCase(alias)) {
                                use = true;
                                break;
                            }
                        }
                    }
                    if (use) {
                        if (sender.hasPermission(pluginPermission + ".*")) {
                            cmd.perform(sender, args, command);
                        } else if (cmd.getPermission().equalsIgnoreCase("")) {
                            cmd.perform(sender, args, command);
                        } else if (!cmd.getPermission().equalsIgnoreCase("") && sender.hasPermission(cmd.getPermission())) {
                            cmd.perform(sender, args, command);
                        } else {
                            sender.sendMessage(UChat.component(noPermissionMsg));
                        }
                        ran = true;
                    }
                }
                if (!ran) {
                    sender.sendMessage(UChat.component("&cUnknown sub-command: &8" + args[0]));
                }
            }
        }

        return true;
    }

    public SubCommand getSubCommand(String subCommandName) {
        for (SubCommand command : getSubcommands()) {
            if (command.getName().equalsIgnoreCase(subCommandName)) {
                return command;
            }
        }
        return null;
    }

    public void printHelpMsg(CommandSender sender) {
        if (sender instanceof Player) {
            sender.sendMessage(UChat.getSeparator(ChatColor.BLUE));
        }
        sender.sendMessage(new CenteredMessage("&9" + pluginTitle + " commands:").getString());
        for (String cmdStr : getSubCommandsOnlyWithPerms(sender)) {
            if (sender.hasPermission(pluginPermission + ".*")) {
                printHelpMsgSpecific(sender, getSubCommand(cmdStr).getDescription(), getSubCommand(cmdStr).getUsage(), getSubCommand(cmdStr).getPermission(), Arrays.toString(getSubCommand(cmdStr).getAliases()));
            } else if (getSubCommand(cmdStr).getPermission().equalsIgnoreCase("")) {
                printHelpMsgSpecific(sender, getSubCommand(cmdStr).getDescription(), getSubCommand(cmdStr).getUsage(), getSubCommand(cmdStr).getPermission(), Arrays.toString(getSubCommand(cmdStr).getAliases()));
            } else if (!getSubCommand(cmdStr).getPermission().equalsIgnoreCase("") && sender.hasPermission(getSubCommand(cmdStr).getPermission())) {
                printHelpMsgSpecific(sender, getSubCommand(cmdStr).getDescription(), getSubCommand(cmdStr).getUsage(), getSubCommand(cmdStr).getPermission(), Arrays.toString(getSubCommand(cmdStr).getAliases()));
            }
        }
        if (sender instanceof Player) {
            sender.sendMessage(UChat.getSeparator(ChatColor.BLUE));
        }
    }

    public ArrayList<SubCommand> getSubcommands() {
        return subcommands;
    }

    public ArrayList<String> getSubCommandsOnlyWithPerms(CommandSender sender) {
        ArrayList<String> cmdNames = new ArrayList<>();
        for (SubCommand cmd : getSubcommands()) {
            if (sender.hasPermission(pluginPermission + ".*")) {
                cmdNames.add(cmd.getName());
            } else if (cmd.getPermission().equalsIgnoreCase("")) {
                cmdNames.add(cmd.getName());
            } else if (!cmd.getPermission().equalsIgnoreCase("") && sender.hasPermission(cmd.getPermission())) {
                cmdNames.add(cmd.getName());
            }
        }
        return cmdNames;
    }

    @Override
    public List<String> tab(@NotNull CommandSender sender, @NotNull String command, CommandInformation information, String[] args) {
        if (getSubCommandsOnlyWithPerms(sender).size() <= 1) {
            return UTabComp.emptyList;
        } else if (args.length <= 1) {
            return UTabComp.tabCompletionsSearch(args[0], getSubCommandsOnlyWithPerms(sender));
        } else {
            try {
                for (SubCommand cmd : getSubcommands()) {
                    for (int i = 1; i < 100; i++) {
                        boolean use = args[0].equalsIgnoreCase(cmd.getName());
                        if (!use) {

                            for (String cmdAlias : cmd.getAliases()) {
                                if (args[0].equalsIgnoreCase(cmdAlias)) {
                                    use = true;
                                    break;
                                }
                            }
                        }
                        if (use) {
                            if (args.length - 1 == i) {
                                if (sender.hasPermission(pluginPermission + ".*")) {
                                    return UTabComp.tabCompletionsSearch(args[i], cmd.tab(sender, args));
                                } else if (cmd.getPermission().equalsIgnoreCase("")) {
                                    return UTabComp.tabCompletionsSearch(args[i], cmd.tab(sender, args));
                                } else if (!cmd.getPermission().equalsIgnoreCase("") && sender.hasPermission(cmd.getPermission())) {
                                    return UTabComp.tabCompletionsSearch(args[i], cmd.tab(sender, args));
                                } else {
                                    return UTabComp.emptyList;
                                }
                            }
                        }
                    }
                }
            } catch (Exception exception) {
                BetterStackTraces.print(exception);
                return UTabComp.emptyList;
            }
        }
        return UTabComp.emptyList;
    }
}
