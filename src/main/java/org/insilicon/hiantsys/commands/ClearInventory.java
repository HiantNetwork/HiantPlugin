package org.insilicon.hiantsys.commands;

import net.cybercake.cyberapi.spigot.chat.UChat;
import net.cybercake.cyberapi.spigot.chat.UTabComp;
import net.cybercake.cyberapi.spigot.server.commands.CommandInformation;
import net.cybercake.cyberapi.spigot.server.commands.SpigotCommand;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.insilicon.hiantsys.Config;
import org.insilicon.hiantsys.Utils.HiantUtils;
import org.insilicon.hiantsys.Utils.PlayerOption;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("unused")
public class ClearInventory extends SpigotCommand {
    public ClearInventory() {
        super(
                newCommand("clearinventory")
                        .setDescription("Clear your or another players inventory")
                        .setPermission("hiant.clearinventory")
                        .setUsage("/ci [player]")
                        .setAliases("ci", "clearinv")
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
                        players.forEach((p) -> clearInventory(sender, p));
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

        clearInventory(sender, (Player) target);

        return true;
    }

    public void clearInventory(CommandSender sender, Player target) {
        target.getInventory().clear();
        target.playSound(target.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
        sender.sendMessage(UChat.component(Config.getPrefix() + "&eYou cleared &e" + (target.equals(sender) ? "your" : target.getName() + "&e's") + " &einventory."));

        if (!target.equals(sender))
            target.sendMessage(UChat.component(Config.getPrefix() + "&eYour inventory has been &acleared&e."));
    }

    @Override
    public List<String> tab(@NotNull CommandSender sender, @NotNull String command, CommandInformation information, String[] args) {
        if (args.length != 1) return UTabComp.emptyList;

        return UTabComp.tabCompletionsSearch(args[0], HiantUtils.addPlayerOptions());
    }
}
