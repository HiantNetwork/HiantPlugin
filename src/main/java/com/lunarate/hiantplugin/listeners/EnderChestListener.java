package com.lunarate.hiantplugin.listeners;


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

