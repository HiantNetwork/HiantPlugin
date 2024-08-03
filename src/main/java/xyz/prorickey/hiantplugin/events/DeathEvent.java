package xyz.prorickey.hiantplugin.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathEvent implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getPlayer();
        Player killer = e.getEntity().getKiller();
        EntityDamageEvent.DamageCause damageCause = player.getLastDamageCause().getCause();
        Component weapon = null;
        if(killer != null &&
                killer.getInventory().getItemInMainHand().getItemMeta() != null &&
                killer.getInventory().getItemInMainHand().getItemMeta().displayName() != null) weapon = killer.getInventory().getItemInMainHand().getItemMeta().displayName();
        switch(damageCause) {
            case BLOCK_EXPLOSION, ENTITY_ATTACK, ENTITY_SWEEP_ATTACK -> {
                if(killer != null) {
                    Component message = MiniMessage.miniMessage().deserialize("<red>\u2620 <gold>" + player.getName() + "<yellow> was murdered by <gold>" + killer.getName());
                    if(weapon != null) message = message.append(MiniMessage.miniMessage().deserialize("<yellow> using ").append(weapon));
                    e.deathMessage(message);
                }
                else e.deathMessage(MiniMessage.miniMessage().deserialize("<red>\u2620 <gold>" + player.getName() + "<yellow> was slain"));
            }
            case PROJECTILE -> {
                if(killer != null) {
                    Component message = MiniMessage.miniMessage().deserialize("<red>\u2620 <gold>" + player.getName() + "<yellow> was sniped by <gold>" + killer.getName());
                    if(weapon != null) message = message.append(MiniMessage.miniMessage().deserialize("<yellow> using ").append(weapon));
                    e.deathMessage(message);
                }
                else e.deathMessage(MiniMessage.miniMessage().deserialize("<red>\u2620 <gold>" + player.getName() + "<yellow> died by a projectile"));
            }
            case FALL -> e.deathMessage(MiniMessage.miniMessage().deserialize("<red>\u2620 <gold>" + player.getName() + "<yellow> fell to their death"));
            case LAVA -> e.deathMessage(MiniMessage.miniMessage().deserialize("<red>\u2620 <gold>" + player.getName() + "<yellow> took a swim in lava"));
            case VOID -> e.deathMessage(MiniMessage.miniMessage().deserialize("<red>\u2620 <gold>" + player.getName() + "<yellow> jumped into the abyss"));
            case FIRE -> e.deathMessage(MiniMessage.miniMessage().deserialize("<red>\u2620 <gold>" + player.getName() + "<yellow> was cremated"));
            case MAGIC -> e.deathMessage(MiniMessage.miniMessage().deserialize("<red>\u2620 <gold>" + player.getName() + "<yellow> died to a magic spell"));
            case DRYOUT -> e.deathMessage(MiniMessage.miniMessage().deserialize("<red>\u2620 <gold>" + player.getName() + "<yellow> dried out?"));
            case FREEZE -> e.deathMessage(MiniMessage.miniMessage().deserialize("<red>\u2620 <gold>" + player.getName() + "<yellow> froze to death"));
            case POISON -> e.deathMessage(MiniMessage.miniMessage().deserialize("<red>\u2620 <gold>" + player.getName() + "<yellow> was poisoned"));
            case THORNS -> e.deathMessage(MiniMessage.miniMessage().deserialize("<red>\u2620 <gold>" + player.getName() + "<yellow> was pricked by too many thorns"));
            case WITHER -> e.deathMessage(MiniMessage.miniMessage().deserialize("<red>\u2620 <gold>" + player.getName() + "<yellow> withered away"));
            case MELTING -> e.deathMessage(MiniMessage.miniMessage().deserialize("<red>\u2620 <gold>" + player.getName() + "<yellow> melted?"));
            case SUICIDE -> e.deathMessage(MiniMessage.miniMessage().deserialize("<red>\u2620 <gold>" + player.getName() + "<yellow> killed themself"));
            case CRAMMING -> e.deathMessage(MiniMessage.miniMessage().deserialize("<red>\u2620 <gold>" + player.getName() + "<yellow> got Astroworlded"));
            case DROWNING -> e.deathMessage(MiniMessage.miniMessage().deserialize("<red>\u2620 <gold>" + player.getName() + "<yellow> drowned"));
            case FIRE_TICK -> e.deathMessage(MiniMessage.miniMessage().deserialize("<red>\u2620 <gold>" + player.getName() + "<yellow> burned alive"));
            case HOT_FLOOR -> e.deathMessage(MiniMessage.miniMessage().deserialize("<red>\u2620 <gold>" + player.getName() + "<yellow> failed the floor is lava challenge"));
            case LIGHTNING -> e.deathMessage(MiniMessage.miniMessage().deserialize("<red>\u2620 <gold>" + player.getName() + "<yellow> got struck by lightning"));
            case SONIC_BOOM -> e.deathMessage(MiniMessage.miniMessage().deserialize("<red>\u2620 <gold>" + player.getName() + "<yellow> died from a sonic boom"));
            case STARVATION -> e.deathMessage(MiniMessage.miniMessage().deserialize("<red>\u2620 <gold>" + player.getName() + "<yellow> starved to death"));
            case SUFFOCATION -> e.deathMessage(MiniMessage.miniMessage().deserialize("<red>\u2620 <gold>" + player.getName() + "<yellow> suffocated"));
            case DRAGON_BREATH -> e.deathMessage(MiniMessage.miniMessage().deserialize("<red>\u2620 <gold>" + player.getName() + "<yellow> died from the dragon's breath"));
            case FALLING_BLOCK -> e.deathMessage(MiniMessage.miniMessage().deserialize("<red>\u2620 <gold>" + player.getName() + "<yellow> died by a falling block"));
            case FLY_INTO_WALL -> e.deathMessage(MiniMessage.miniMessage().deserialize("<red>\u2620 <gold>" + player.getName() + "<yellow> flew into a wall"));
            case ENTITY_EXPLOSION -> e.deathMessage(MiniMessage.miniMessage().deserialize("<red>\u2620 <gold>" + player.getName() + "<yellow> died from an explosion"));
            default -> e.deathMessage(MiniMessage.miniMessage().deserialize("<red>\u2620 <gold>" + player.getName() + "<yellow> died"));
        }
    }

}
