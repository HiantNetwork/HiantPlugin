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
import org.insilicon.hiantsys.Hiantsys;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("unused")
public class TPAll extends SpigotCommand {
    public TPAll() {
        super(
                newCommand("tpall")
                        .setDescription("Brings all players to you or another player")
                        .setPermission("hiant.tpall")
                        .setAliases("bringall")
                        .setUsage("/tpall [player]")
                        .build()
        );
    }

    @Override
    public boolean perform(@NotNull CommandSender sender, @NotNull String command, CommandInformation information, String[] args) {
        if (!(sender instanceof Player player)) {
            Log.error("You must be a player to execute this command");
            return true;
        }

        Player target = player;
        if (args.length != 0) target = Bukkit.getPlayerExact(args[0]);

        if (target == null) {
            sender.sendMessage(UChat.component(Config.getErrorPrefix() + "&4" + args[0] + " &cis not currently online."));
            return true;
        }

        tpAllToPlayer(player, target);

        return true;
    }

    public void tpAllToPlayer(Player player, Player target) {
        Bukkit.getOnlinePlayers().forEach(p -> p.teleport(target.getLocation()));
        player.sendMessage(UChat.component(Config.getPrefix() + "&eYou have brought all players to &a" + (target.equals(player) ? "yourself" : target.getName()) + "&e."));

        if (!player.equals(target))
            target.sendMessage(UChat.component(Config.getPrefix() + "&eAll &aonline players &ehave been teleported to You."));
    }

    @Override
    public List<String> tab(@NotNull CommandSender sender, @NotNull String command, CommandInformation information, String[] args) {
        if (args.length != 1) return UTabComp.emptyList;

        return UTabComp.tabCompletionsSearch(args[0], Hiantsys.getInstance().getOnlinePlayersUsernames());
    }
}