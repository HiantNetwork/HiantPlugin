package org.insilicon.hiantplugin.schedules;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ScheduledBroadcasts implements Runnable {

    public static void startBroadcasts(JavaPlugin plugin) {
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(
                plugin,
                new ScheduledBroadcasts(),
                0,
                20*60*5
        );
    }

    private static int nextBroadcast = 0;
    private static final List<Component> broadcasts = List.of(
            Component.text("\n\n")
                    .append(MiniMessage.miniMessage().deserialize("""
                                                 <#EEEB1B><bold>CLICK HERE</bold></#EEEB1B><gradient:#49B6FF:#31ACFF> to join the Hiant Discord.
                                               <#2CEE1B>/link</#2CEE1B> to claim an extra Starter Crate Key!
                                      """)
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/nKDPz2pmQb"))
                    ).append(Component.text("\n")),
            Component.text("\n\n")
                    .append(MiniMessage.miniMessage().deserialize("""
                                          <#EEEB1B><bold>CLICK HERE</bold></#EEEB1B><gradient:#48E513:#41E40A> to open the store catalog and
                                             view the ranks, perks, and crate keys</gradient>
                                   """)
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/store"))
                    ).append(Component.text("\n"))
    );

    @Override
    public void run() {
        Component bc = broadcasts.get(nextBroadcast);
        Sound sound = Sound.sound(Key.key("block.note_block.bit"), Sound.Source.MASTER, 1f, 1f);
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.sendMessage(bc);
            player.playSound(sound);
        }
        if(broadcasts.size()-1 == nextBroadcast) nextBroadcast = 0;
        else nextBroadcast++;
    }

}
