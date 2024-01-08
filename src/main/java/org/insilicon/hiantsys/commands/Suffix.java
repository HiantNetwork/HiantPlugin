package org.insilicon.hiantsys.commands;

import net.cybercake.cyberapi.spigot.chat.TabCompleteType;
import net.cybercake.cyberapi.spigot.chat.UChat;
import net.cybercake.cyberapi.spigot.server.commands.CommandInformation;
import net.cybercake.cyberapi.spigot.server.commands.SpigotCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.insilicon.hiantsys.guis.Suffixes;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("unused")
public class Suffix extends SpigotCommand {
    public Suffix() {
        super(
                newCommand("suffix")
                        .setDescription("Opens the suffix selector.")
                        .setUsage("/suffix")
                        .setAliases("setsuffix")
                        .setPermission("hiant.suffix")
                        .setTabCompleteType(TabCompleteType.SEARCH)
        );
    }

    @Override
    public boolean perform(@NotNull CommandSender sender, @NotNull String command, CommandInformation information, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(UChat.component("&cOnly players may execute this command!"));
            return true;
        }

        new Suffixes(player, 1, 1).open(player);

        return true;
    }

    @Override
    public List<String> tab(@NotNull CommandSender sender, @NotNull String command, CommandInformation information, String[] args) {
        return null;
    }
}
