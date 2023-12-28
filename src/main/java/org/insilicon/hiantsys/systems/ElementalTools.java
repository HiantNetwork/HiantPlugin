package org.insilicon.hiantsys.systems;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.insilicon.hiantsys.hiantsys;

public class ElementalTools implements Listener {

    public NamespacedKey key = hiantsys.getPlugin(hiantsys.class).key;

    public ElementalTools() {

    }




    //On Kill Event
    @EventHandler
    public void onKill(EntityDeathEvent event) {
        System.out.println("EntityDeathEvent");
        if (event.getEntity().getKiller() instanceof Player && event.getEntity() instanceof Player) {
            System.out.println("Both players");
        } else {
            return;
        }
        //Get attacker and victim
        Player attacker = (Player) event.getEntity().getKiller();
        Player victim = (Player) event.getEntity();


        //Get the item the victim was killed with
        ItemStack item = attacker.getInventory().getItemInMainHand();
        System.out.println(item);

        System.out.println(hiantsys.getPlugin(hiantsys.class).key);



        //Check if the item has the ElementalItem key
        if (item.getItemMeta().getPersistentDataContainer().has(hiantsys.getPlugin(hiantsys.class).key, org.bukkit.persistence.PersistentDataType.STRING)) {
            System.out.println("Player killed with ElementalItem");

            //Get the value of the ElementalItem
            String ElementalValue = item.getItemMeta().getPersistentDataContainer().get(hiantsys.getPlugin(hiantsys.class).key, org.bukkit.persistence.PersistentDataType.STRING);


            if (ElementalValue.equals("Fire")) {

                //Make multiple fire particles fly out of the victim like an explosion
                spawnFireExplosion(victim.getLocation());



            }
        }




    }

    //Animations

    private void spawnFireExplosion(Location location) {
        int numberOfParticles = 700; // Adjust the number of particles as needed
        double speed = 1; // Increased speed for a more explosive effect

        for (int i = 0; i < numberOfParticles; i++) {
            Vector direction = Vector.getRandom().subtract(new Vector(0.5, 0.5, 0.5)).normalize();
            location.getWorld().spawnParticle(Particle.FLAME, location, 0, direction.getX(), direction.getY(), direction.getZ(), speed);
        }
    }



}
