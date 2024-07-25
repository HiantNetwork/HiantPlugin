package com.lunarate.hiantplugin.listeners;


import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class EnderChestListener implements Listener {
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();

        if (holder instanceof Player) {
            Player player = (Player) holder;
            

            if (upgradedEnderChest != null && inventory.getType() == InventoryType.ENDER_CHEST) {
                event.setCancelled(true);
                player.openInventory(upgradedEnderChest);
            }
        }
    }
}

