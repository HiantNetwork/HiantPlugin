package org.insilicon.hiantplugin.systems;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.insilicon.hiantplugin.HiantPlugin;

import java.util.ArrayList;
import java.util.List;

public class MechanicalElytra implements Listener  {

    List<Player> playersWithMechanicalElytra;

    public MechanicalElytra() {

        //Empty ArrayList
        playersWithMechanicalElytra = new ArrayList<>();



    }


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        //CHeck if they are no longer flying or if they are flying
        if (event.getPlayer().isGliding()) {
            //if player is wearing elytra with pdt key of mechanicalelytra
            if (event.getPlayer().getInventory().getChestplate() != null && event.getPlayer().getInventory().getChestplate().getType().equals(Material.ELYTRA) && event.getPlayer().getInventory().getChestplate().getItemMeta().getPersistentDataContainer().has(HiantPlugin.getPlugin(HiantPlugin.class).mechanicalelytrakey, org.bukkit.persistence.PersistentDataType.STRING)) {

            } else {
                return;
            }
            //Add to list if they are not already in it (check by uuid)
            for (Player player : playersWithMechanicalElytra) {
                if (player.getUniqueId().equals(event.getPlayer().getUniqueId())) {
                    handleInteraction(event);
                    return;
                }
            }
            playersWithMechanicalElytra.add(event.getPlayer());
            System.out.println("Added player to list");
        } else {
            //Find player in list by uuid
            for (Player player : playersWithMechanicalElytra) {
                if (player.getUniqueId().equals(event.getPlayer().getUniqueId())) {
                    //Remove from list
                    playersWithMechanicalElytra.remove(player);
                    System.out.println("Removed player from list");
                }
            }

        }






    }


    public void handleInteraction(PlayerMoveEvent event) {
        System.out.println("Handling interaction");


        //increase velocity by 1 bps until at 35 bps
        Player player = event.getPlayer();
        double speed = event.getTo().toVector().subtract(event.getFrom().toVector()).length() * 20; // Speed in blocks per second
        //Get the value in the persistent data container of the elytra
        //Get elytra
        ItemStack elytra = player.getInventory().getChestplate();

        System.out.println();
        if (speed < elytra.getItemMeta().getPersistentDataContainer().get(HiantPlugin.getPlugin().mechanicalelytrakey, PersistentDataType.INTEGER) && player.isSneaking()) {
            System.out.println("Velocity increased");

            // Get the player's current looking direction
            Vector direction = player.getLocation().getDirection();

            // Optionally, you may want to normalize and scale the direction vector
            // For example, to add a fixed amount of velocity regardless of how far the player is looking up or down

            direction.normalize().multiply((1.0 / 20.0)+1); // 'someScalingFactor' is a double that determines how much to increase the velocity

            // Increase the player's velocity in the direction they are looking
            player.setVelocity(player.getVelocity().add(direction));

            //Play minecart sound
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_MINECART_RIDING, 1.0f, 2.0f);
        }


    }


    //



}
