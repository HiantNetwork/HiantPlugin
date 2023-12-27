package org.insilicon.hiantsys.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.insilicon.hiantsys.CustomClasses.CustomGUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class hsys_admin implements CommandExecutor, TabCompleter, Listener {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        //Check if sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to use this command!");
            return true;
        }

        //Get first argument
        String arg1 = args[0];

        switch (arg1) {
            case "help":
                sender.sendMessage("Use /hs admin for the admin panel");
                return true;

            case "admin":
                sender.sendMessage("&aYou are now in the admin panel");
                //Create inventory
                CustomGUI gui = new CustomGUI(9, ChatColor.GREEN + "HiantSys" + ChatColor.RED + " Admin Panel");
                //Get inventory
                Inventory inv = gui.getInventory();

                //Get Dark Gray Stained Glass Pane item
                ItemStack grypane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                //Get Green Stained Glass Pane item
                ItemStack givebtn = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);

//Set display name of items
                ItemMeta grypanemeta = grypane.getItemMeta();
                grypanemeta.setDisplayName(" ");

                ItemMeta givebtnmeta = givebtn.getItemMeta();
                givebtnmeta.setDisplayName(ChatColor.GREEN + "Give");

//Set item meta
                grypane.setItemMeta(grypanemeta);
                givebtn.setItemMeta(givebtnmeta);


//Add items to inventory
                inv.setItem(0, grypane);
                inv.setItem(1, grypane);
                inv.setItem(2, grypane);
                inv.setItem(3, grypane);
                inv.setItem(4, givebtn);
                inv.setItem(5, grypane);
                inv.setItem(6, grypane);
                inv.setItem(7, grypane);
                inv.setItem(8, grypane);

//Open inventory
                ((Player) sender).openInventory(inv);




                return true;
        }


        return true;

    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //Add completions for the command [ help, admin ]

        //Make array of completions
        List<String> completions = new ArrayList<>();

        completions.add("help");
        completions.add("admin");

        //Return completions

        return completions;


    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.GREEN + "HiantSys" + ChatColor.RED + " Admin Panel")) {
            //Get name of item clicked
            String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

            event.setCancelled(true);
        }
    }
}
