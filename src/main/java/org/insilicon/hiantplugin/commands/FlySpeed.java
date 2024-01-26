package org.insilicon.hiantplugin.commands;

import net.cybercake.cyberapi.spigot.chat.Log;
import net.cybercake.cyberapi.spigot.chat.TabCompleteType;
import net.cybercake.cyberapi.spigot.chat.UChat;
import net.cybercake.cyberapi.spigot.server.commands.CommandInformation;
import net.cybercake.cyberapi.spigot.server.commands.SpigotCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.insilicon.hiantplugin.Config;
import org.insilicon.hiantplugin.HiantPlugin;
import org.insilicon.hiantplugin.Utils.HiantUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class FlySpeed extends SpigotCommand {
    public int max = -1;
    public int min = -1;

    public FlySpeed() {
        super(
                newCommand("flyspeed")
                        .setDescription("Sets your flyspeed")
                        .setPermission("hiant.flyspeed")
                        .setUsage("/flyspeed <speed>")
                        .setTabCompleteType(TabCompleteType.SEARCH)
        );
    }

    public void speed() {
        max = HiantPlugin.getConf().getInt("speed.maximum", 10);
        min = HiantPlugin.getConf().getInt("speed.minimum", 0);
    }

    @Override
    public boolean perform(@NotNull CommandSender sender, @NotNull String command, CommandInformation information, String[] args) {
        if (max == -1) speed();

        if (!(sender instanceof Player player)) {
            Log.error("You must be a player to execute this command");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(UChat.component(Config.getErrorPrefix() + "&4Invalid usage! &c" + information.getUsage()));
            return true;
        }

        float speed = Float.parseFloat(args[0]);
        if (speed > max || speed < min) {
            sender.sendMessage(UChat.component(Config.getErrorPrefix() + "&cYour speed cannot be " + (speed > max ? "higher" : "lower") + " than " + (speed > max ? max : min) + "!"));
            return true;
        }

        speed = speed / 10;

        player.setFlySpeed(speed);
        player.sendMessage(HiantUtils.coloredMiniMessage(Config.getPrefix() + "&eYour fly speed has been set to &a" + Math.round(speed * 10)));

        return true;

    }

    @Override
    public List<String> tab(@NotNull CommandSender sender, @NotNull String command, CommandInformation information, String[] args) {
        if (max == -1) speed();

        if (args.length == 1) {
            ArrayList<String> numbers = new ArrayList<>();
            for (int i = min; i < max; i++) {
                numbers.add(String.valueOf(i));
            }
            return numbers;
        }

        return null;
    }
}
