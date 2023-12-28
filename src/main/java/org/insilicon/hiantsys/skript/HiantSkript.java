package org.insilicon.hiantsys.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import org.insilicon.hiantsys.Hiantsys;

public class HiantSkript {

    SkriptAddon addon;

    public HiantSkript() {
        addon = Skript.registerAddon(Hiantsys.getPlugin());
        try {
            addon.loadClasses("org.insilicon.hiantsys.skript", "elements");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SkriptAddon getAddonInstance() {
        return addon;
    }

}
