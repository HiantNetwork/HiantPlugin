package org.insilicon.hiantplugin;

import ch.njol.skript.SkriptAddon;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import fr.mrmicky.fastinv.FastInvManager;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import net.cybercake.cyberapi.common.builders.settings.FeatureSupport;
import net.cybercake.cyberapi.common.builders.settings.Settings;
import net.cybercake.cyberapi.spigot.CyberAPI;
import net.cybercake.cyberapi.spigot.basic.BetterStackTraces;
import net.cybercake.cyberapi.spigot.chat.Log;
import net.cybercake.cyberapi.spigot.config.Config;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.codehaus.plexus.util.FileUtils;
import org.insilicon.hiantplugin.commands.main.CommandManager;
import org.insilicon.hiantplugin.configuration.RecipesConfig;
import xyz.prorickey.hiantplugin.EnderChestHandler;
import xyz.prorickey.hiantplugin.LibertyBansHook;
import xyz.prorickey.hiantplugin.commands.EnderChest;
import xyz.prorickey.hiantplugin.commands.LinkCommand;
import xyz.prorickey.hiantplugin.commands.UnlinkCommand;
import xyz.prorickey.hiantplugin.database.Database;
import xyz.prorickey.hiantplugin.discord.HiantDiscord;
import xyz.prorickey.hiantplugin.events.DeathEvent;
import xyz.prorickey.hiantplugin.events.PlayerJoin;
import xyz.prorickey.hiantplugin.schedules.ScheduledBroadcasts;
import org.insilicon.hiantplugin.skript.HiantSkript;
import org.insilicon.hiantplugin.systems.Regeneration;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

@SuppressWarnings({"unused", "deprecation"})
public final class HiantPlugin extends CyberAPI {

    private static HiantPlugin plugin;

    public NamespacedKey elementalnamespacekey;
    public NamespacedKey mechanicalelytrakey;

    public FileConfiguration config;
    public World world;
    private static SkriptAddon hiantSkriptAddon;
    private static LuckPerms luckPermsAPI;
    private static Database database;
    private static HiantDiscord discord;

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void onEnable() {
        // Plugin startup logic
        long mss = System.currentTimeMillis();
        plugin = this;

        elementalnamespacekey = new NamespacedKey(HiantPlugin.getPlugin(HiantPlugin.class), "ElementalItem");
        mechanicalelytrakey = new NamespacedKey(HiantPlugin.getPlugin(HiantPlugin.class), "MechanicalElytra");

        HiantSkript skript = new HiantSkript();
        hiantSkriptAddon = skript.getAddonInstance();

        Log.info("Custom hiant skript stuff loaded");

        //Register commands / tab completers / Listener
        //this.getCommand("hiantsys").setExecutor(new org.insilicon.hiantsys.commands.hsys_admin());
        //this.getCommand("hiantsys").setTabCompleter(new org.insilicon.hiantsys.commands.hsys_admin());
        new CommandManager();
        getServer().getPluginManager().registerEvents(new org.insilicon.hiantplugin.commands.hsys_admin(), this);

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

        // Proickey - initialize database and discord client after config is loaded
        database = new Database();
        discord = new HiantDiscord();

        // Prorickey - register liberty bans hook
        LibertyBansHook.create();

        // Prorickey - register commands
        LifecycleEventManager<Plugin> manager = this.getLifecycleManager();
        LinkCommand.register(manager);
        UnlinkCommand.register(manager);
        EnderChest.register(manager);
        
        // Prorickey - register events
        getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
        getServer().getPluginManager().registerEvents(new EnderChestHandler(), this);
        getServer().getPluginManager().registerEvents(new DeathEvent(), this);

        //Init FAWE world edit api
        world = BukkitAdapter.adapt(getServer().getWorld("box"));

        Log.info("FAWE API initialized");

        getServer().getPluginManager().registerEvents(new org.insilicon.hiantplugin.systems.ElementalTools(), this);
        getServer().getPluginManager().registerEvents(new org.insilicon.hiantplugin.commands.hsys_admin(), this);

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
        getServer().getPluginManager().registerEvents(new org.insilicon.hiantplugin.systems.MechanicalElytra(), this);

        Config RecipesConfigClass = RecipesConfig.getConfig();
        try {
            RecipesConfigClass.copyDefaults();
            RecipesConfigClass.saveDefaults();
            RecipesConfigClass.reload();
        } catch (IOException e) {
            Log.error("Error loading recipes config");
            BetterStackTraces.print(e);
        }

        ScheduledBroadcasts.startBroadcasts(this);

        Log.info("&aLoaded " + getDescription().getName() + " [v" + getDescription().getVersion() + "], created by " + String.join(" ", getDescription().getAuthors()) + " in " + (System.currentTimeMillis() - mss) + "ms! ");
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "regeneration.yml"));

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        discord.getJDA().shutdown();
        try {
            if(!discord.getJDA().awaitShutdown(Duration.ofSeconds(10))) {
                discord.getJDA().shutdownNow();
                discord.getJDA().awaitShutdown();
            }
        } catch (InterruptedException e) {
            getLogger().severe("Discord bot failed to shutdown: " + e.getMessage());
        }
    }

    public static HiantPlugin getPlugin() {
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

    public static Database getDatabase() { return database; }
    public static HiantDiscord getDiscord() { return discord; }

}
