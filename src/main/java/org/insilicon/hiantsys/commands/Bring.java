package org.insilicon.hiantsys.commands;

import net.cybercake.cyberapi.spigot.chat.Log;
import net.cybercake.cyberapi.spigot.chat.UChat;
import net.cybercake.cyberapi.spigot.chat.UTabComp;
import net.cybercake.cyberapi.spigot.server.commands.CommandInformation;
import net.cybercake.cyberapi.spigot.server.commands.SpigotCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.insilicon.hiantsys.Config;
import org.insilicon.hiantsys.Utils.HiantUtils;
import org.insilicon.hiantsys.Utils.PlayerOption;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("unused")
public class Bring extends SpigotCommand {
    public Bring() {
        super(
                newCommand("bring")
                        .setDescription("Brings a player to you")
                        .setPermission("hiant.bring")
                        .setUsage("/bring <player>")
                        .setAliases("tphere")
                        .build()
        );
    }

    @Override
    public boolean perform(@NotNull CommandSender sender, @NotNull String command, CommandInformation information, String[] args) {
        if (!(sender instanceof Player player)) {
            Log.error("You must be a player to execute this command");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(UChat.component(Config.getErrorPrefix() + "&4Invalid usage! &c" + information.getUsage()));
            return true;
        }

        PlayerOption addOptions = HiantUtils.addPlayerOptions(args[0]);
        if (addOptions != null) {
            HiantUtils.addPlayerOptions(sender, addOptions, (players) -> {
                if (players.isEmpty()) return;
                players.forEach((p) -> bringPlayer(player, p));
            });

            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(UChat.component(Config.getErrorPrefix() + "&4" + args[0] + " &cis not currently online!"));
            return true;
        }

        bringPlayer(player, target);

        return true;
    }

    public void bringPlayer(Player player, Player target) {
        target.teleport(player.getLocation());
        player.sendMessage(UChat.component(Config.getPrefix() + "&eYou brought &a" + (target.equals(player) ? "yourself" : target.getName())));
    }

    @Override
    public List<String> tab(@NotNull CommandSender sender, @NotNull String command, CommandInformation information, String[] args) {
        if (args.length != 1) return UTabComp.emptyList;

        return UTabComp.tabCompletionsSearch(args[0], HiantUtils.addPlayerOptions());
    }
}
