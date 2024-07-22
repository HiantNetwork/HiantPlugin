package xyz.prorickey.hiantplugin.commands;

import com.mojang.brigadier.Command;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.insilicon.hiantplugin.HiantPlugin;
import xyz.prorickey.hiantplugin.database.Database;

public class UnlinkCommand {

    @SuppressWarnings("UnstableApiUsage")
    public static void register(LifecycleEventManager<Plugin> manager) {
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register(
                    Commands.literal("unlink")
                            .executes(context -> {
                                handle(context.getSource().getSender());
                                return Command.SINGLE_SUCCESS;
                            })
                            .build(),
                    "Link your Minecraft account to your Discord account"
            );
        });
    }

    private static void handle(CommandSender sender) {
        if(!(sender instanceof Player player)) return;
        Database.LinkedAccount linkedAccount = HiantPlugin.getDatabase().getLinkedAccount(player.getUniqueId());
        if(linkedAccount != null) {
            HiantPlugin.getDatabase().unlinkAccount(player.getUniqueId());
            player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Your account has been unlinked successfully!"));
        } else {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>You do not have a linked account."));
        }

    }

}
