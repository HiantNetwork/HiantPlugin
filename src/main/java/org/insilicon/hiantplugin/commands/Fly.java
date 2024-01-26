package org.insilicon.hiantplugin.commands;

import net.cybercake.cyberapi.spigot.chat.UChat;
import net.cybercake.cyberapi.spigot.chat.UTabComp;
import net.cybercake.cyberapi.spigot.server.commands.CommandInformation;
import net.cybercake.cyberapi.spigot.server.commands.SpigotCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.insilicon.hiantplugin.Config;
import org.insilicon.hiantplugin.Utils.HiantUtils;
import org.insilicon.hiantplugin.Utils.PlayerOption;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("unused")
public class Fly extends SpigotCommand {
    public Fly() {
        super(
                newCommand("fly")
                        .setDescription("Enables or disables flight mode for you or another player")
                        .setPermission("hiant.fly")
                        .setUsage("/fly [player]")
                        .build()
        );
    }

    @Override
    public boolean perform(@NotNull CommandSender sender, @NotNull String command, CommandInformation information, String[] args) {
        if (args.length == 0 && !(sender instanceof Player player)) {
            sender.sendMessage(UChat.component(Config.getErrorPrefix() + "&4Invalid usage! &c" + information.getUsage()));
            return true;
        }

        CommandSender target = sender;

        if (sender.hasPermission(information.getPermission() + ".others")) {
            if (args.length > 0) {
                PlayerOption addOptions = HiantUtils.addPlayerOptions(args[0]);
                if (addOptions != null) {
                    HiantUtils.addPlayerOptions(sender, addOptions, (players) -> {
                        if (players.isEmpty()) return;
                        players.forEach((p) -> toggleFlight(sender, p));
                    });

                    return true;
                }
            }

            if (args.length != 0) target = Bukkit.getPlayerExact(args[0]);

            if (target == null) {
                sender.sendMessage(UChat.component(Config.getErrorPrefix() + "&4" + args[0] + " &cis not currently online."));
                return true;
            }
        }

        toggleFlight(sender, (Player) target);

        return true;
    }

    public void toggleFlight(CommandSender sender, Player target) {
        target.setAllowFlight(!target.getAllowFlight());
        target.setFlying(target.getAllowFlight());
        sender.sendMessage(UChat.component(Config.getPrefix() + "&eYou set &e" + (target.equals(sender) ? "your" : target.getName() + "&e's") + " &eflight mode to " + (target.getAllowFlight() ? "&atrue" : "&cfalse") + "&e!"));

        if (!target.equals(sender))
            target.sendMessage(UChat.component(Config.getPrefix() + "&eYour &7flight mode was set to " + (target.getAllowFlight() ? "&atrue" : "&cfalse")));
    }

    @Override
    public List<String> tab(@NotNull CommandSender sender, @NotNull String command, CommandInformation information, String[] args) {
        if (sender.hasPermission(information.getPermission() + ".others")) {
            if (args.length != 1) return null;

            return UTabComp.tabCompletionsSearch(args[0], HiantUtils.addPlayerOptions());
        }

        return null;
    }
}
