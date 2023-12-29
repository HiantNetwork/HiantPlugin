package org.insilicon.hiantsys;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Hiantsys extends JavaPlugin {

    private static Hiantsys plugin;
    public NamespacedKey elementalnamespacekey;
    public NamespacedKey mechanicalelytrakey;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        elementalnamespacekey = new NamespacedKey(Hiantsys.getPlugin(Hiantsys.class), "ElementalItem");
        mechanicalelytrakey = new NamespacedKey(Hiantsys.getPlugin(Hiantsys.class), "MechanicalElytra");

        //Register commands / tab completers / Listener
        this.getCommand("hiantsys").setExecutor(new org.insilicon.hiantsys.commands.hsys_admin());
        this.getCommand("hiantsys").setTabCompleter(new org.insilicon.hiantsys.commands.hsys_admin());
        getServer().getPluginManager().registerEvents(new org.insilicon.hiantsys.commands.hsys_admin(), this);


        //Elemental Registration
        getServer().getPluginManager().registerEvents(new org.insilicon.hiantsys.systems.ElementalTools(), this);

        //Mechanical Elytra Registration
        getServer().getPluginManager().registerEvents(new org.insilicon.hiantsys.systems.MechanicalElytra(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Hiantsys getPlugin() {
        return plugin;
    }


}
