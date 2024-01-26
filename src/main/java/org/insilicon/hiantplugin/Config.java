package org.insilicon.hiantplugin;

public class Config {

    public static String getPrefix() {
        return HiantPlugin.getConf().getString("prefix");
    }

    public static String getWarnPrefix() {
        return HiantPlugin.getConf().getString("warnPrefix");
    }

    public static String getErrorPrefix() {
        return HiantPlugin.getConf().getString("errorPrefix");
    }
}