package org.insilicon.hiantplugin.commands;

import net.cybercake.cyberapi.spigot.chat.TabCompleteType;
import net.cybercake.cyberapi.spigot.chat.UChat;
import net.cybercake.cyberapi.spigot.server.commands.CommandInformation;
import net.cybercake.cyberapi.spigot.server.commands.SpigotCommand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.insilicon.hiantplugin.Config;
import org.insilicon.hiantplugin.Utils.HiantUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"unused"})
public class Sudo extends SpigotCommand {
    public Sudo() {
        super(
                newCommand("sudo")
                        .setDescription("Makes a player execute a command or send a chat message")
                        .setPermission("hiant.sudo")
                        .setUsage("/sudo <user> (/ | c:)<string>")
                        .setTabCompleteType(TabCompleteType.SEARCH)
                        .build()
        );
    }

    @Override
    public boolean perform(@NotNull CommandSender sender, @NotNull String command, CommandInformation information, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(UChat.component(Config.getErrorPrefix() + "&4Invalid Usage! &c" + information.getUsage()));
            return true;
        }

        String ptStringVal = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        if (args[0].equalsIgnoreCase("*") || args[0].equalsIgnoreCase("all") || args[0].equalsIgnoreCase("@a")) {
            Bukkit.getOnlinePlayers().forEach(p -> {
                sender.getServer().dispatchCommand(sender, "sudo " + p.getName() + " " + ptStringVal);
            });
            return true;
        } else if (args[0].equalsIgnoreCase("others") || args[0].equalsIgnoreCase("other")) {
            Bukkit.getOnlinePlayers().forEach(p -> {
                if (p.equals(sender)) return;
                sender.getServer().dispatchCommand(sender, "sudo " + p.getName() + " " + ptStringVal);
            });
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(UChat.component(Config.getErrorPrefix() + "&4Invalid Usage! &c" + information.getUsage()));
            return true;
        }

        String type = "command";

        String todo = ptStringVal.replace("/", "").replace("c:", "");

        if (args[1].toLowerCase().startsWith("c:")) {
            type = "message";
        }

        if (type.equalsIgnoreCase("message"))
            target.chat(todo);
        else
            target.getServer().dispatchCommand(target, todo);

        sender.sendMessage(UChat.component(Config.getPrefix() + "&eMade " + target.getName() + " &eexecute &a" + type + " &8| &a" + ptStringVal.replace("c:", "")));
        return true;
    }

    @Override
    public List<String> tab(@NotNull CommandSender sender, @NotNull String command, CommandInformation information, String[] args) {
        if (!(sender instanceof Player player)) return null;

        if (args.length == 1)
            return HiantUtils.addPlayerOptions();

        if (args.length == 2) {
            if (player.getInventory().getItemInMainHand().getType() == Material.AIR) return null;
            if (args[0].startsWith("/") || args[0].toLowerCase().startsWith("c:")) return null;

            return new ArrayList<>(Arrays.asList("/", "c:"));
        }

        return null;
    }
}
