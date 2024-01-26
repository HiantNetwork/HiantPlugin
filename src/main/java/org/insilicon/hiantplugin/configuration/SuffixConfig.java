package org.insilicon.hiantplugin.configuration;

import net.cybercake.cyberapi.spigot.config.Config;
import org.bukkit.configuration.file.FileConfiguration;

public class SuffixConfig {
    public static Config getConfig() {
        return new Config("suffixes");
    }

    public static FileConfiguration getFileConfig() {
        return new Config("suffixes").values();
    }
}
