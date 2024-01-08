package org.insilicon.hiantsys.commands.main.subcommands;

import net.cybercake.cyberapi.spigot.chat.UChat;
import net.cybercake.cyberapi.spigot.chat.UTabComp;
import org.bukkit.command.CommandSender;
import org.insilicon.hiantsys.commands.main.SubCommand;

import java.util.List;

public class Help extends SubCommand {

    public Help() {
        super("help", "", "Prints this help message.", "help", "?", "info");
    }

    @Override
    public void perform(CommandSender sender, String[] args, String command) {
        // Overridden and moved to main CommandManager class
        sender.sendMessage(UChat.component("&cAn error occurred!"));
    }

    @Override
    public List<String> tab(CommandSender sender, String[] args) {
        return UTabComp.emptyList;
    }

}
