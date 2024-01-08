package org.insilicon.hiantsys.commands.main.subcommands;

import net.cybercake.cyberapi.common.basic.Time;
import net.cybercake.cyberapi.spigot.chat.UChat;
import net.cybercake.cyberapi.spigot.chat.UTabComp;
import net.voidedsky.common.Config;
import net.voidedsky.common.Main;
import net.voidedsky.common.Utils.CommonUtils;
import net.voidedsky.common.commands.main.SubCommand;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Reload extends SubCommand {

    private static long isReloading = -1L;

    public Reload() {
        super("reload", "voidedsky.common.maincmd.reload", "Reloads the plugin's configuration files!", "reload", "rl");
    }

    @Override
    public void perform(CommandSender sender, String[] args, String command) {
        if (isReloading != -1L) {
            sender.sendMessage(UChat.component(Config.getErrorPrefix() + "&cThe server is currently reloading. It's been reloading for &6" + Time.getBetterTimeDisplay(Time.getUnix(), isReloading) + "&c!"));
            if (Time.getUnix() - isReloading >= 60) isReloading = -1L;
            return;
        }

        isReloading = Time.getUnix();
        long mss = System.currentTimeMillis();

        Exception exception = null;
        String exceptionFile = "";

        try {
            Main.getInstance().saveDefaultConfig();
            Main.getInstance().reloadConfig();
        } catch (Exception ex) {
            exception = ex;
            exceptionFile = "config.yml";
        }

        if (exception != null) {
            sender.sendMessage(CommonUtils.coloredMiniMessage("whilst trying to reload the " + exceptionFile + " file!"));
            Main.getInstance().playSound(sender, Sound.BLOCK_NOTE_BLOCK_BASS, 1F, 1F);
        }

        sender.sendMessage(UChat.component("&6You successfully reloaded the configuration files in &a" + (System.currentTimeMillis() - mss) + "&ams&6!"));
        isReloading = -1L;

        Main.getInstance().playSound(sender, Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
    }

    @Override
    public List<String> tab(CommandSender sender, String[] args) {
        return UTabComp.emptyList;
    }

}
