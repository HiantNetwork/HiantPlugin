package org.insilicon.hiantplugin.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import net.cybercake.cyberapi.spigot.basic.BetterStackTraces;
import net.cybercake.cyberapi.spigot.chat.Log;
import org.insilicon.hiantplugin.HiantPlugin;
import org.insilicon.hiantplugin.skript.elements.effects.EffClearBox;
import org.insilicon.hiantplugin.skript.elements.effects.EffResetCastle;
import org.insilicon.hiantplugin.skript.elements.expressions.ExprConvertDoubleChestToBlock;
import org.insilicon.hiantplugin.skript.elements.expressions.ExprDefaultItemName;

public class HiantSkript {

    SkriptAddon addon;

    public HiantSkript() {
        try {
            if (Skript.getAddon(HiantPlugin.getPlugin()) != null) {
                Log.warn("Skript has already been loaded, SKIPPING.");
                addon = Skript.getAddon(HiantPlugin.getPlugin());
                return;
            }
            Log.info("Loading skript things...");
            addon = Skript.registerAddon(HiantPlugin.getPlugin());
            addon.loadClasses("org.insilicon.hiantplugin.skript", "elements");

            new EffClearBox();
            new EffResetCastle();
            new ExprConvertDoubleChestToBlock();
            new ExprDefaultItemName();

            Log.info("Loaded all skript things");
        } catch (Exception e) {
            Log.error(e.getMessage());
            Log.info("Failed to load skript");
        }
    }

    public SkriptAddon getAddonInstance() {
        return addon;
    }
}