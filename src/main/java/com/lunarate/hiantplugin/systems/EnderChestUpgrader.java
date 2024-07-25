package com.lunarate.hiantplugin.systems;



public class EnderChestUpgrader {
    public static void upgradeEnderChest(Player player) {
        Inventory enderChest = player.getEnderChest();
        ItemStack[] oldContents = enderChest.getContents();
        Inventory upgradedEnderChest = player.getServer().createInventory(player, 54, "Upgraded Ender Chest");

        for (int i = 0; i < oldContents.length; i++) {
            if (oldContents[i] != null) {
                upgradedEnderChest.setItem(i, oldContents[i]);
            }
        }

        // Do not know how to store the data efficiently
    }
}


