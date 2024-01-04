package org.insilicon.hiantsys.commands;

import net.cybercake.cyberapi.common.basic.NumberUtils;
import net.cybercake.cyberapi.spigot.chat.UChat;
import net.cybercake.cyberapi.spigot.chat.UTabComp;
import net.cybercake.cyberapi.spigot.server.commands.CommandInformation;
import net.cybercake.cyberapi.spigot.server.commands.SpigotCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.insilicon.hiantsys.Config;
import org.insilicon.hiantsys.Hiantsys;
import org.insilicon.hiantsys.Utils.HiantUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("unused")
public class Speed extends SpigotCommand {
    public int max = -1;
    public int min = -1;

    public Speed() {
        super(
                newCommand("speed")
                        .setDescription("Sets you or another player's walking or flight speed to a certain number")
                        .setPermission("hiant.speed")
                        .setUsage("/speed <speed> [fly/walk/both] [player]")
                        .build()
        );
    }

    public void speed() {
        max = Hiantsys.getConf().getInt("speed.maximum", 10);
        min = Hiantsys.getConf().getInt("speed.minimum", 1);
    }

    @Override
    public boolean perform(@NotNull CommandSender sender, @NotNull String command, CommandInformation information, String[] args) {
        if (max == -1) speed();

        if (!(sender instanceof Player player)) {
            if (args.length < 3) {
                sender.sendMessage(UChat.component(Config.getErrorPrefix() + "&cInvalid usage! &7/speed <speed> <fly|walk|both> <player>"));
            }
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(UChat.component(Config.getErrorPrefix() + "&cInvalid usage! &7/speed <speed> [fly/walk/both] [player]"));
            return true;
        }

        if (!NumberUtils.isFloat(args[0])) {
            sender.sendMessage(UChat.component(Config.getErrorPrefix() + "&cInvalid usage! &7/speed <speed> [fly/walk/both] [player]"));
            return true;
        }

        float speed = Float.parseFloat(args[0]);
        if (speed > max || speed < min) {
            sender.sendMessage(UChat.component(Config.getErrorPrefix() + "&cYour speed cannot be " + (speed > max ? "higher" : "lower") + " than " + (speed > max ? max : min) + "!"));
            return true;
        }

        speed = speed / 10;
        if (args.length == 2) {
            String type = args[1];
            if (type.equals("both")) {
                type = "flight&walking";
            }
            setSpeed(player, sender, speed, type);
            return true;
        } else if (args.length == 3) {
            String type = args[1];
            if (type.equals("both")) {
                type = "flight&walking";
            }
            Player target = Bukkit.getPlayerExact(args[2]);
            setSpeed(target, sender, speed, type);
            return true;
        }

        if (player.isFlying()) {
            setSpeed(player, sender, speed, "fly");
        } else {
            setSpeed(player, sender, speed, "walk");
        }

        return true;

    }

    public void setSpeed(Player set, CommandSender msg, float speed, String type) {
        boolean both = false;
        switch (type) {
            case "fly", "flight" -> set.setFlySpeed(speed);
            case "walk", "walking" -> set.setWalkSpeed(speed);
            case "flight&walking" -> {
                both = true;
                set.setFlySpeed(speed);
                set.setWalkSpeed(speed);
            }
            default -> {
                msg.sendMessage(UChat.component(Config.getErrorPrefix() + "&cInvalid speed type! &7/speed <speed> [fly/walk] [player]"));
                return;
            }
        }

        String beginning = Config.getPrefix() + "&eYou set " + (set.equals(msg) ? "your" : set.getName() + "&e's");
        String ending = "&espeed to &a" + Math.round(speed * 10) + "&e!";
        if (both) {
            msg.sendMessage(UChat.component(beginning + " &eflight and walk " + ending));
            return;
        }
        msg.sendMessage(UChat.component(beginning + " &e" + type.toLowerCase(Locale.ROOT) + " " + ending));
    }

    @Override
    public List<String> tab(@NotNull CommandSender sender, @NotNull String command, CommandInformation information, String[] args) {
        if (max == -1) speed();

        if (args.length == 1) {
            ArrayList<String> numbers = new ArrayList<>();
            for (int i = min; i < max; i++) {
                numbers.add(String.valueOf(i));
            }
            return UTabComp.tabCompletionsSearch(args[0], numbers);
        } else if (args.length == 2) {
            return UTabComp.tabCompletionsSearch(args[1], Arrays.asList("fly", "walk", "both"));
        } else if (args.length == 3) {
            return UTabComp.tabCompletionsSearch(args[2], HiantUtils.addPlayerOptions());
        }
        return UTabComp.emptyList;
    }
}
