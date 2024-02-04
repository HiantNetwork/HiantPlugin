package org.insilicon.hiantplugin.systems;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.insilicon.hiantplugin.HiantPlugin;
public class ElementalTools implements Listener {

    public NamespacedKey key = HiantPlugin.getPlugin(HiantPlugin.class).elementalnamespacekey;

    public ElementalTools() {

    }


    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            //System.out.println("Both players");
        } else {
            return;
        }

        Player attacker = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();


        ItemStack item = attacker.getInventory().getItemInMainHand();
        //System.out.println(item);

        //System.out.println(HiantPlugin.getPlugin(HiantPlugin.class).elementalnamespacekey);

        if (item.getItemMeta().getPersistentDataContainer().has(HiantPlugin.getPlugin(HiantPlugin.class).elementalnamespacekey, org.bukkit.persistence.PersistentDataType.STRING)) {
            //System.out.println("Player killed with ElementalItem");

            //Get the value of the ElementalItem
            String ElementalValue = item.getItemMeta().getPersistentDataContainer().get(HiantPlugin.getPlugin(HiantPlugin.class).elementalnamespacekey, org.bukkit.persistence.PersistentDataType.STRING);


            if (ElementalValue.equals("Fire")) {

                //Make multiple fire particles fly out of the victim like an explosion
                spawnFireExplosion(victim.getLocation(), 0.5, 300);

                if (attacker != null) {
                    // Play sound for the killer
                    //Check if the item they are holding is a bow
                    if (attacker.getInventory().getItemInMainHand().getType().toString().toLowerCase().contains("bow")) {
                        Sound soundForKiller = Sound.BLOCK_NOTE_BLOCK_PLING; // Example sound
                        attacker.playSound(attacker.getLocation(), soundForKiller, 0.5f, 1.0f);
                    } else {

                    }



                }


            }
        }

    }


    //On Kill Event
    @EventHandler
    public void onKill(EntityDeathEvent event) {
        //System.out.println("EntityDeathEvent");
        if (event.getEntity().getKiller() instanceof Player && event.getEntity() instanceof Player) {
            //System.out.println("Both players");
        } else {
            return;
        }
        //Get attacker and victim
        Player attacker = (Player) event.getEntity().getKiller();
        Player victim = (Player) event.getEntity();


        //Get the item the victim was killed with
        ItemStack item = attacker.getInventory().getItemInMainHand();
        //System.out.println(item);

        //System.out.println(HiantPlugin.getPlugin(HiantPlugin.class).elementalnamespacekey);



        //Check if the item has the ElementalItem key
        if (item.getItemMeta().getPersistentDataContainer().has(HiantPlugin.getPlugin(HiantPlugin.class).elementalnamespacekey, org.bukkit.persistence.PersistentDataType.STRING)) {
            //System.out.println("Player killed with ElementalItem");

            //Get the value of the ElementalItem
            String ElementalValue = item.getItemMeta().getPersistentDataContainer().get(HiantPlugin.getPlugin(HiantPlugin.class).elementalnamespacekey, org.bukkit.persistence.PersistentDataType.STRING);


            if (ElementalValue.equals("Fire")) {

                //Make multiple fire particles fly out of the victim like an explosion
                spawnFireExplosion(victim.getLocation(), 1, 700);

                if (attacker != null) {
                    // Play sound for the killer
                    Sound soundForKiller = Sound.ITEM_FIRECHARGE_USE; // Example sound
                    attacker.playSound(attacker.getLocation(), soundForKiller, 1.0f, 1.0f);

                    // Play sound for the victim
                    Sound soundForVictim = Sound.ITEM_FIRECHARGE_USE; // Example sound
                    victim.playSound(victim.getLocation(), soundForVictim, 1.0f, 1.0f);
                }


            }
        }




    }

    //Animations

    private void spawnFireExplosion(Location location, double speed, int numberOfParticles) {


        for (int i = 0; i < numberOfParticles; i++) {
            Vector direction = Vector.getRandom().subtract(new Vector(0.5, 0.5, 0.5)).normalize();
            location.getWorld().spawnParticle(Particle.FLAME, location, 0, direction.getX(), direction.getY(), direction.getZ(), speed);
        }

        //Play fire_charge sound

    }



}
