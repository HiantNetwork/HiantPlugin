package org.insilicon.hiantplugin.commands;

import net.cybercake.cyberapi.spigot.chat.UChat;
import net.cybercake.cyberapi.spigot.server.commands.CommandInformation;
import net.cybercake.cyberapi.spigot.server.commands.SpigotCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.insilicon.hiantplugin.CustomClasses.CustomGUI;
import org.insilicon.hiantplugin.HiantPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class hsys_admin extends SpigotCommand implements Listener {

    public hsys_admin() {
        super(
                newCommand("hiantsys")
                        .setDescription("Hiant system admin")
                        .setPermission("hiantsys.admin")
                        .setDescription("hiantsys")
                        .setUsage("/hiantsys [argument]")
                        .setAliases("hs")
                        .build()
        );
    }

    @Override
    public boolean perform(@NotNull CommandSender sender, @NotNull String command, CommandInformation commandInformation, String[] args) {

        //Check if sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to use this command!");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(UChat.component("&cInvalid usage! &7" + commandInformation.getUsage()));
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
                // Purple Stained Glass Pane aka Regeneration Settings
                ItemStack regenbtn = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);

//Set display name of items
                ItemMeta grypanemeta = grypane.getItemMeta();
                grypanemeta.setDisplayName(" ");

                ItemMeta givebtnmeta = givebtn.getItemMeta();
                givebtnmeta.setDisplayName(ChatColor.GREEN + "Give");

                ItemMeta regenbtnmeta = regenbtn.getItemMeta();
                regenbtnmeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Regeneration Settings");


//Set item meta
                grypane.setItemMeta(grypanemeta);
                givebtn.setItemMeta(givebtnmeta);
                regenbtn.setItemMeta(regenbtnmeta);


//Add items to inventory
                inv.setItem(0, grypane);
                inv.setItem(1, grypane);
                inv.setItem(2, regenbtn);
                inv.setItem(3, grypane);
                inv.setItem(4, givebtn);
                inv.setItem(5, grypane);
                inv.setItem(6, grypane);
                inv.setItem(7, grypane);
                inv.setItem(8, grypane);

//Open inventory
                ((Player) sender).openInventory(inv);




                return true;
            default:
                sender.sendMessage("Invalid argument");

        }


        return true;

    }

    @Override
    public List<String> tab(@NotNull CommandSender commandSender, @NotNull String s, CommandInformation commandInformation, String[] strings) {
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

            if (itemName.equals(ChatColor.GREEN + "Give")) {

                //Close inventory
                event.getWhoClicked().closeInventory();

                //Open item give inventory
                CustomGUI givegui = new CustomGUI(9, ChatColor.GREEN + "HiantSys" + ChatColor.RED + " Give Panel");
                Inventory giveinv = givegui.getInventory();

                //Create ItemStacks
                ItemStack FireForgedDiamondSword = new ItemStack(Material.DIAMOND_SWORD);


                //Set up displays

                //FireForgedDiamondSword

                ItemMeta FireForgedDiamondSwordmeta = FireForgedDiamondSword.getItemMeta();
                FireForgedDiamondSwordmeta.setDisplayName(ChatColor.RED + "FireForged Diamond Sword");
                FireForgedDiamondSwordmeta.addEnchant(Enchantment.FIRE_ASPECT, 1, true);
                FireForgedDiamondSword.setItemMeta(FireForgedDiamondSwordmeta);

                //Add items to inventory
                giveinv.setItem(0, FireForgedDiamondSword);


                //Fire bow
                ItemStack FireBow = new ItemStack(Material.BOW);
                ItemMeta FireBowmeta = FireBow.getItemMeta();
                FireBowmeta.setDisplayName(ChatColor.RED + "Fire Bow");
                FireBowmeta.addEnchant(Enchantment.FLAME, 1, true);
                FireBow.setItemMeta(FireBowmeta);

                giveinv.setItem(1, FireBow);



                //Mechanical Elytra
                ItemStack MechanicalElytra = new ItemStack(Material.ELYTRA);

                ItemMeta MechanicalElytrameta = MechanicalElytra.getItemMeta();
                MechanicalElytrameta.setDisplayName(ChatColor.GRAY +""+ ChatColor.BOLD + "Mechanical Elytra");
                MechanicalElytrameta.addEnchant(Enchantment.INFINITY, 1, true);
                MechanicalElytrameta.getPersistentDataContainer().set(HiantPlugin.getPlugin(HiantPlugin.class).mechanicalelytrakey, PersistentDataType.STRING, "MechanicalElytra");

                //Lore Array
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.LIGHT_PURPLE + "Crafted by the most genius of villagers!");

                MechanicalElytrameta.setLore(lore);
                MechanicalElytrameta.setUnbreakable(true);

                MechanicalElytra.setItemMeta(MechanicalElytrameta);

                giveinv.setItem(2, MechanicalElytra);


                //Open inventory
                ((Player) event.getWhoClicked()).openInventory(giveinv);


            }

            if (itemName.equals(ChatColor.LIGHT_PURPLE + "Regeneration Settings")) {
                //close inventory
                event.getWhoClicked().closeInventory();

                //Open Regeneration Settings inventory
                CustomGUI regengui = new CustomGUI(9, ChatColor.DARK_PURPLE + "HiantSys" + ChatColor.LIGHT_PURPLE + " Regeneration Settings");
                Inventory regeninv = regengui.getInventory();

                ItemStack enabled = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                ItemStack disabled = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                ItemStack reload = new ItemStack(Material.COMPASS);

                //Set up displays
                ItemMeta enabledmeta = enabled.getItemMeta();
                enabledmeta.setDisplayName(ChatColor.GREEN + "Enabled");
                enabled.setItemMeta(enabledmeta);

                ItemMeta disabledmeta = disabled.getItemMeta();
                disabledmeta.setDisplayName(ChatColor.RED + "Disabled");
                disabled.setItemMeta(disabledmeta);

                ItemMeta reloadmeta = reload.getItemMeta();
                reloadmeta.setDisplayName(ChatColor.DARK_PURPLE + "Reload");
                reload.setItemMeta(reloadmeta);

                FileConfiguration config = HiantPlugin.getPlugin(HiantPlugin.class).getConfig();
                if (config.getBoolean("regeneration.enabled")) {
                    regeninv.setItem(1, enabled);
                } else {
                    regeninv.setItem(1, disabled);
                }

                regeninv.setItem(8, reload);

                //Show inventory
                ((Player) event.getWhoClicked()).openInventory(regeninv);



            }

            event.setCancelled(true);
        }

        if (event.getView().getTitle().equals(ChatColor.GREEN + "HiantSys" + ChatColor.RED + " Give Panel")) {
            //Get name of item clicked
            String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

            if (itemName.equals(ChatColor.RED + "FireForged Diamond Sword")) {

                ItemStack FireForgedDiamondSword = new ItemStack(Material.DIAMOND_SWORD);
                ItemMeta FireForgedDiamondSwordmeta = FireForgedDiamondSword.getItemMeta();
                FireForgedDiamondSwordmeta.setDisplayName(ChatColor.RED + "FireForged Diamond Sword");
                FireForgedDiamondSwordmeta.addEnchant(Enchantment.FIRE_ASPECT, 1, true);

                System.out.println(HiantPlugin.getPlugin(HiantPlugin.class).elementalnamespacekey);

                FireForgedDiamondSwordmeta.getPersistentDataContainer().set(HiantPlugin.getPlugin(HiantPlugin.class).elementalnamespacekey, PersistentDataType.STRING, "Fire");



                FireForgedDiamondSword.setItemMeta(FireForgedDiamondSwordmeta);

                FireForgedDiamondSword.setAmount(1);

                event.getWhoClicked().getInventory().addItem(FireForgedDiamondSword);



            }

            if (itemName.equals(ChatColor.RED + "Fire Bow")) {

                ItemStack FireBow = new ItemStack(Material.BOW);
                ItemMeta FireBowmeta = FireBow.getItemMeta();
                FireBowmeta.setDisplayName(ChatColor.RED + "Fire Bow");
                FireBowmeta.addEnchant(Enchantment.FLAME, 1, true);

                System.out.println(HiantPlugin.getPlugin(HiantPlugin.class).elementalnamespacekey);

                FireBowmeta.getPersistentDataContainer().set(HiantPlugin.getPlugin(HiantPlugin.class).elementalnamespacekey, PersistentDataType.STRING, "Fire");

                FireBow.setItemMeta(FireBowmeta);

                FireBow.setAmount(1);

                event.getWhoClicked().getInventory().addItem(FireBow);

            }

            if (itemName.equals(ChatColor.GRAY +""+ ChatColor.BOLD + "Mechanical Elytra")) {

                ItemStack MechanicalElytra = new ItemStack(Material.ELYTRA);

                ItemMeta MechanicalElytrameta = MechanicalElytra.getItemMeta();
                MechanicalElytrameta.setDisplayName(ChatColor.GRAY +""+ ChatColor.BOLD + "Mechanical Elytra");
                MechanicalElytrameta.addEnchant(Enchantment.INFINITY, 1, true);
                MechanicalElytrameta.getPersistentDataContainer().set(HiantPlugin.getPlugin(HiantPlugin.class).mechanicalelytrakey, PersistentDataType.INTEGER, 35);

                //Lore Array
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.LIGHT_PURPLE + "Crafted by the most genius of villagers!");

                MechanicalElytrameta.setLore(lore);
                MechanicalElytrameta.setUnbreakable(true);

                MechanicalElytra.setItemMeta(MechanicalElytrameta);

                MechanicalElytra.setAmount(1);

                event.getWhoClicked().getInventory().addItem(MechanicalElytra);


            }

            event.setCancelled(true);
        }


        if (event.getView().getTitle().equals(ChatColor.DARK_PURPLE + "HiantSys" + ChatColor.LIGHT_PURPLE + " Regeneration Settings")) {

            String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
            ItemStack enabled = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            ItemStack disabled = new ItemStack(Material.RED_STAINED_GLASS_PANE);

            //Set up displays
            ItemMeta enabledmeta = enabled.getItemMeta();
            enabledmeta.setDisplayName(ChatColor.GREEN + "Enabled");
            enabled.setItemMeta(enabledmeta);

            ItemMeta disabledmeta = disabled.getItemMeta();
            disabledmeta.setDisplayName(ChatColor.RED + "Disabled");
            disabled.setItemMeta(disabledmeta);

            if (itemName.equals(ChatColor.GREEN + "Enabled")) {

                //On the config set regeneration.enabled to false and save the config
                FileConfiguration config = HiantPlugin.getPlugin(HiantPlugin.class).getConfig();
                config.set("regeneration.enabled", false);
                event.getView().setItem(1, disabled);


            }
            if (itemName.equals(ChatColor.RED + "Disabled")) {

                FileConfiguration config = HiantPlugin.getPlugin(HiantPlugin.class).getConfig();
                config.set("regeneration.enabled", true);
                event.getView().setItem(1, enabled);

            }

            if (itemName.equals(ChatColor.DARK_PURPLE + "Reload")) {
                event.getView().close();
                //Get user
                Player player = (Player) event.getWhoClicked();

                player.sendMessage(ChatColor.DARK_PURPLE + "Reloading...");
                HiantPlugin.getPlugin(HiantPlugin.class).reloadConfig();
                player.sendMessage(ChatColor.GREEN + "Reloaded!");
            }

            event.setCancelled(true);

        }



    }
}
