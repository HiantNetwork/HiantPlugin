package xyz.prorickey.hiantplugin.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.insilicon.hiantplugin.HiantPlugin;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        HiantPlugin.getDatabase().cacheData(event.getPlayer().getUniqueId());
    }

}
