package org.insilicon.hiantplugin.commands.main.subcommands;

import net.cybercake.cyberapi.spigot.chat.UChat;
import net.cybercake.cyberapi.spigot.chat.UTabComp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;
import org.insilicon.hiantplugin.commands.main.SubCommand;

import java.util.List;

@SuppressWarnings("deprecation")
public class Tasks extends SubCommand {

    public Tasks() {
        super("tasks", "hiant.main.tasks", "List and/or cancel certain tasks", "tasks", "gettasks");
    }

    @Override
    public void perform(CommandSender sender, String[] args, String command) {
        sender.sendMessage(UChat.component("&b&lSYNC &btasks ->"));
        for (BukkitTask worker : Bukkit.getScheduler().getPendingTasks()) {
            sender.sendMessage(UChat.component("&ctask id&f: " + worker.getTaskId() + " &8|| &6plugin owner&f: " + worker.getOwner().getDescription().getName() + " &8|| &ecancelled&f? " + worker.isCancelled() + " &8|| &bsync&f? " + worker.isSync()));
        }
        sender.sendMessage(UChat.getSeparator(ChatColor.BOLD));
        sender.sendMessage(UChat.component("&b&lASYNC &btasks ->"));
        Bukkit.getScheduler().getActiveWorkers().forEach(bukkitWorker -> {
            sender.sendMessage(UChat.component("&ctask id&f: " + bukkitWorker.getTaskId() + " &8|| &6plugin owner&f: " + bukkitWorker.getOwner().getDescription().getName() + " &8|| &eThread name & id&f? " + bukkitWorker.getThread().getName() + "/" + bukkitWorker.getThread().getId()));
        });
    }

    @Override
    public List<String> tab(CommandSender sender, String[] args) {
        if (args.length == 3 && args[1].equalsIgnoreCase("cancel"))
            return UTabComp.tabCompletionsSearch(args[2], List.of("FAILED_TO_GET_TASKS"));
        if (args.length == 2) return UTabComp.tabCompletionsSearch(args[1], List.of("list", "cancel"));
        return UTabComp.emptyList;
    }
}
