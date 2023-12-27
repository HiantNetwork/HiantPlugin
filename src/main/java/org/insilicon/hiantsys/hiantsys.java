package org.insilicon.hiantsys;

import org.bukkit.plugin.java.JavaPlugin;

public final class hiantsys extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        //Register commands / tab completers / Listener
        this.getCommand("hiantsys").setExecutor(new org.insilicon.hiantsys.commands.hsys_admin());
        this.getCommand("hiantsys").setTabCompleter(new org.insilicon.hiantsys.commands.hsys_admin());
        getServer().getPluginManager().registerEvents(new org.insilicon.hiantsys.commands.hsys_admin(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
