package org.insilicon.hiantsys;

import ch.njol.skript.SkriptAddon;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import fr.mrmicky.fastinv.FastInvManager;
import net.cybercake.cyberapi.common.builders.settings.FeatureSupport;
import net.cybercake.cyberapi.common.builders.settings.Settings;
import net.cybercake.cyberapi.spigot.CyberAPI;
import net.cybercake.cyberapi.spigot.basic.BetterStackTraces;
import net.cybercake.cyberapi.spigot.chat.Log;
import net.cybercake.cyberapi.spigot.config.Config;
import net.cybercake.cyberapi.spigot.player.CyberPlayer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.node.types.SuffixNode;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.codehaus.plexus.util.FileUtils;
import org.insilicon.hiantsys.configuration.RecipesConfig;
import org.insilicon.hiantsys.skript.HiantSkript;
import org.insilicon.hiantsys.systems.Regeneration;

import java.io.File;
import java.io.IOException;

@SuppressWarnings({"unused", "deprecation"})
public final class Hiantsys extends CyberAPI {

    private static Hiantsys plugin;

    public NamespacedKey elementalnamespacekey;
    public NamespacedKey mechanicalelytrakey;

    public FileConfiguration config;
    public World world;
    private static SkriptAddon hiantSkriptAddon;
    private static LuckPerms luckPermsAPI;


    @Override
    public void onEnable() {
        // Plugin startup logic
        long mss = System.currentTimeMillis();
        plugin = this;

        elementalnamespacekey = new NamespacedKey(Hiantsys.getPlugin(Hiantsys.class), "ElementalItem");
        mechanicalelytrakey = new NamespacedKey(Hiantsys.getPlugin(Hiantsys.class), "MechanicalElytra");

        //Register commands / tab completers / Listener
        this.getCommand("hiantsys").setExecutor(new org.insilicon.hiantsys.commands.hsys_admin());
        this.getCommand("hiantsys").setTabCompleter(new org.insilicon.hiantsys.commands.hsys_admin());
        getServer().getPluginManager().registerEvents(new org.insilicon.hiantsys.commands.hsys_admin(), this);

        startCyberAPI(
                Settings.builder()
                        .mainPackage("org.insilicon.hiantsys")
                        .prefix("Hiant")
                        .showPrefixInLogs(true)
                        .luckPermsSupport(FeatureSupport.SUPPORTED)
                        .build()
        );

        FastInvManager.register(this);
        Log.info("FastINV registered successfully");

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPermsAPI = provider.getProvider();
            Log.info("Loaded into luckperms successfully");
        }else {
            throw new RuntimeException("Luckperms API failed to load!");
        }

        //Config Sys
        try {
            initConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        copyDefaultConfig();
        saveDefaultConfig();
        reloadConfig();
        Log.info("Main config(s) loaded");


        //Init FAWE world edit api
        world = BukkitAdapter.adapt(getServer().getWorld("box"));

        Log.info("FAWE API initialized");

        HiantSkript skript = new HiantSkript();
        hiantSkriptAddon = skript.getAddonInstance();

        Log.info("Custom hiant skript stuff loaded");

        getServer().getPluginManager().registerEvents(new org.insilicon.hiantsys.systems.ElementalTools(), this);
        getServer().getPluginManager().registerEvents(new org.insilicon.hiantsys.commands.hsys_admin(), this);

        Log.info("Successfully registered all events");

        //Regeneration Registration
        //If enabled
        if (config.getBoolean("regeneration.enabled")) {
            Log.info("Regeneration is enabled");
            getServer().getPluginManager().registerEvents(new Regeneration(), this);
        } else {
            Log.info("Regeneration is disabled");
        }


        //Mechanical Elytra Registration
        getServer().getPluginManager().registerEvents(new org.insilicon.hiantsys.systems.MechanicalElytra(), this);

        Config RecipesConfigClass = RecipesConfig.getConfig();
        try {
            RecipesConfigClass.copyDefaults();
            RecipesConfigClass.saveDefaults();
            RecipesConfigClass.reload();
        } catch (IOException e) {
            Log.error("Error loading recipes config");
            BetterStackTraces.print(e);
        }

        Log.info("&aLoaded " + getDescription().getName() + " [v" + getDescription().getVersion() + "], created by " + String.join(" ", getDescription().getAuthors()) + " in " + (System.currentTimeMillis() - mss) + "ms! ");
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

    public SkriptAddon getHiantSkriptAddon() {
        return hiantSkriptAddon;
    }

    public static LuckPerms getLuckpermsAPI() { return luckPermsAPI; }

    @Override
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
