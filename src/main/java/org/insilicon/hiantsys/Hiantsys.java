package org.insilicon.hiantsys;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.codehaus.plexus.util.FileUtils;
import org.insilicon.hiantsys.systems.Regeneration;

import java.io.File;
import java.io.IOException;

public final class Hiantsys extends JavaPlugin {

    private static Hiantsys plugin;
    public NamespacedKey key;
    public FileConfiguration config;
    public World world;


    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        key = new NamespacedKey(Hiantsys.getPlugin(Hiantsys.class), "ElementalItem");





        //Config Sys

        try {
            initConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        //Init FAWE world edit api
        world = BukkitAdapter.adapt(getServer().getWorld("box"));



        //Register commands / tab completers / Listener
        this.getCommand("hiantsys").setExecutor(new org.insilicon.hiantsys.commands.hsys_admin());
        this.getCommand("hiantsys").setTabCompleter(new org.insilicon.hiantsys.commands.hsys_admin());
        getServer().getPluginManager().registerEvents(new org.insilicon.hiantsys.commands.hsys_admin(), this);


        //Elemental Registration
        getServer().getPluginManager().registerEvents(new org.insilicon.hiantsys.systems.ElementalTools(), this);

        //Regeneration Registration
        //If enabled
        if (config.getBoolean("regeneration.enabled")) {
            System.out.println("Regeneration is enabled");
            getServer().getPluginManager().registerEvents(new Regeneration(), this);
        } else {
            System.out.println(" Regeneration is disabled");
        }

    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "regeneration.yml"));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Hiantsys getPlugin() {
        return plugin;
    }

    public World getWorld() {
        return world;
    }

    public File getFolder() {
        return getDataFolder();
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void initConfig() throws IOException {
        // Create the data folder if it doesn't exist
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        File regenConfigFile = new File(getDataFolder(), "regeneration.yml");

        // Check if the regeneration.nsconfig file exists
        if (!regenConfigFile.exists()) {
            // Save the resource from the JAR to the plugin data folder
            saveResource("regeneration.yml", false);

            // Copy the contents from the predefined configuration file to regeneration.nsconfig
            File preConfigFile = new File(getDataFolder(), "org.insilicon.hiantsys.copy.regeneration.yml");
            if (preConfigFile.exists()) {
                FileUtils.copyFile(preConfigFile, regenConfigFile);
            } else {
                // Handle the case where the preconfig file doesn't exist
                // You might want to log an error or take other actions
            }


        }

        //Check if the nether_castle.schem file exists
        File netherCastleSchemFile = new File(getDataFolder(), "nether_castle.schem");
        if (!netherCastleSchemFile.exists()) {
            // Save the resource from the JAR to the plugin data folder
            saveResource("nether_castle.schem", false);

            // Copy the contents from the predefined configuration file to regeneration.nsconfig
            File preConfigFile = new File(getDataFolder(), "org.insilicon.hiantsys.copy.nether_castle.schem");
            if (preConfigFile.exists()) {
                FileUtils.copyFile(preConfigFile, netherCastleSchemFile);
            } else {
                // Handle the case where the preconfig file doesn't exist
                // You might want to log an error or take other actions
            }
        }


        config = YamlConfiguration.loadConfiguration(regenConfigFile);



    }












}
