package org.insilicon.hiantsys.configuration;

import net.cybercake.cyberapi.spigot.config.Config;
import org.bukkit.configuration.file.FileConfiguration;

public class RecipesConfig {
    public static Config getConfig() {
        return new Config("recipes");
    }

    public static FileConfiguration getFileConfig() {
        return new Config("recipes").values();
    }
}
