package org.insilicon.hiantsys.commands;

import net.cybercake.cyberapi.spigot.chat.Log;
import net.cybercake.cyberapi.spigot.chat.UChat;
import net.cybercake.cyberapi.spigot.chat.UTabComp;
import net.cybercake.cyberapi.spigot.server.commands.CommandInformation;
import net.cybercake.cyberapi.spigot.server.commands.SpigotCommand;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.insilicon.hiantsys.Config;
import org.insilicon.hiantsys.HiantPlugin;
import org.insilicon.hiantsys.Utils.HiantUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

@SuppressWarnings("unused")
public class RandomTeleport extends SpigotCommand {
    public RandomTeleport() {
        super(
                newCommand("randomteleport")
                        .setDescription("Teleports you to a random player on the server")
                        .setAliases("rtp")
                        .setPermission("hiant.randomteleport")
                        .setUsage("/randomteleport")
                        .build()
        );
    }

    public Player getRandomPlayer(Player yourself) {
        List<Player> allPlayers = HiantPlugin.getInstance().getOnlinePlayers();
        allPlayers.remove(yourself);
        if (allPlayers.isEmpty())
            return null;
        int random = new Random().nextInt(allPlayers.size());
        return allPlayers.get(random);
    }

    @Override
    public boolean perform(@NotNull CommandSender sender, @NotNull String command, CommandInformation information, String[] args) {
        if (!(sender instanceof Player player)) {
            Log.error("You must be a player to execute this command");
            return true;
        }

        Player tpToWho = getRandomPlayer(player);
        if (tpToWho == null) {
            player.sendMessage(UChat.chat(Config.getErrorPrefix() + "&cThere is no one to teleport to!"));
            return true;
        }
        while (tpToWho == player) tpToWho = getRandomPlayer(player);

        player.sendMessage(HiantUtils.coloredMiniMessage(Config.getPrefix() + "&eYou have been teleported to &a" + HiantUtils.getDisplayNameIfExists(tpToWho)));
        player.teleport(tpToWho.getLocation());
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1F, 1F);


        return true;
    }

    @Override
    public List<String> tab(@NotNull CommandSender sender, @NotNull String command, CommandInformation information, String[] args) {
        return UTabComp.emptyList;
    }
}
