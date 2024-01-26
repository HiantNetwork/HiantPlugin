package org.insilicon.hiantplugin.CustomClasses;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class CustomGUI {
    private Inventory inv;

    public CustomGUI(int slots, String title) {
        inv = Bukkit.createInventory(null, slots, title);
    }

    public Inventory getInventory() {
        return inv;
    }


}
