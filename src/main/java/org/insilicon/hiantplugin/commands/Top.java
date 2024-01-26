package org.insilicon.hiantplugin.commands;

import net.cybercake.cyberapi.spigot.CyberAPI;
import net.cybercake.cyberapi.spigot.chat.TabCompleteType;
import net.cybercake.cyberapi.spigot.chat.UChat;
import net.cybercake.cyberapi.spigot.chat.UTabComp;
import net.cybercake.cyberapi.spigot.server.commands.CommandInformation;
import net.cybercake.cyberapi.spigot.server.commands.SpigotCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.insilicon.hiantplugin.Config;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("unused")
public class Top extends SpigotCommand {
    public Top() {
        super(
                newCommand("top")
                        .setDescription("Sends you to the top of your current location")
                        .setPermission("voidedsky.common.top")
                        .setUsage("/top")
                        .setTabCompleteType(TabCompleteType.SEARCH)
                        .setPermission("hiant.top")
        );
    }

    @Override
    public boolean perform(@NotNull CommandSender sender, @NotNull String command, CommandInformation information, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(UChat.component("&cOnly players may execute this command!"));
            return true;
        }

        Location topBlock = CyberAPI.getInstance().getTopBlock(player.getLocation());
        if (topBlock == null || topBlock.getBlockY() <= player.getLocation().getBlockY()) {
            player.sendMessage(UChat.component(Config.getErrorPrefix() + "&cYou are already at the to the highest location."));
            return true;
        }

        player.teleport(topBlock);
        player.sendMessage(UChat.component(Config.getPrefix() + "&eYou have been teleported to the highest location."));

        return true;
    }

    @Override
    public List<String> tab(@NotNull CommandSender sender, @NotNull String command, CommandInformation information, String[] args) {
        return UTabComp.emptyList;
    }
}
