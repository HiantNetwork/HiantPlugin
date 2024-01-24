package org.insilicon.hiantsys.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import net.cybercake.cyberapi.spigot.basic.BetterStackTraces;
import net.cybercake.cyberapi.spigot.chat.Log;
import org.insilicon.hiantsys.HiantPlugin;
import org.insilicon.hiantsys.skript.elements.effects.EffClearBox;
import org.insilicon.hiantsys.skript.elements.effects.EffResetCastle;

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
            addon.loadClasses("org.insilicon.hiantsys.skript", "elements");

            new EffClearBox();
            new EffResetCastle();

            Log.info("Loaded all skript things");
        } catch (Exception e) {
            BetterStackTraces.print(e);
        }
    }

    public SkriptAddon getAddonInstance() {
        return addon;
    }
}
