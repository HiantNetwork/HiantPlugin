package org.insilicon.hiantsys;

public class Config {

    public static String getPrefix() {
        return Hiantsys.getConf().getString("prefix");
    }

    public static String getWarnPrefix() {
        return Hiantsys.getConf().getString("warnPrefix");
    }

    public static String getErrorPrefix() {
        return Hiantsys.getConf().getString("errorPrefix");
    }
}