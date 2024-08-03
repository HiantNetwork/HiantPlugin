package xyz.prorickey.hiantplugin.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.insilicon.hiantplugin.HiantPlugin;
import xyz.prorickey.hiantplugin.EnderChestHandler;

import java.util.List;

public class EnderChest {

    @SuppressWarnings("UnstableApiUsage")
    public static void register(LifecycleEventManager<Plugin> manager) {
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register(
                    Commands.literal("enderchest")
                            .requires(source -> source.getSender().hasPermission("hiant.enderchest"))
                            .executes(context -> {
                                handle(context.getSource().getSender());
                                return Command.SINGLE_SUCCESS;
                            })
                            .build(),
                    "Link your Minecraft account to your Discord account",
                    List.of("ec", "echest")
            );

            commands.register(
                    Commands.literal("tebexgiveenderchestupgrade")
                            .requires(source -> source.getSender() instanceof ConsoleCommandSender)
                            .then(
                                    Commands.argument("player", StringArgumentType.string())
                                            .executes(context -> {
                                                handle2(StringArgumentType.getString(context, "player"));
                                                return Command.SINGLE_SUCCESS;
                                            })
                            )
                            .build(),
                    "Console only command to give anywhere ender chest"
            );
        });
    }

    private static void handle(CommandSender sender) {
        if (!(sender instanceof Player player)) return;
        Inventory enderChest = EnderChestHandler.getPlayerEnderChest(player);
        player.openInventory(enderChest);
        player.playSound(Sound.sound(Key.key("block.ender_chest.open"), Sound.Source.MASTER, 1.0f, 1.0f));
    }

    private static void handle2(String playerName) {
        Bukkit.getScheduler().runTaskAsynchronously(HiantPlugin.getPlugin(), () -> {
            OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(playerName);
            EnderChestHandler.upgradePlayerEnderChest(offPlayer.getUniqueId());
        });
    }

}
