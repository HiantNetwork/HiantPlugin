package org.insilicon.hiantplugin.CustomClasses;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.insilicon.hiantplugin.HiantPlugin;

public class ElementalItem {
    private String name;
    private String description;
    public NamespacedKey key = new NamespacedKey(HiantPlugin.getPlugin(HiantPlugin.class), "ElementalItem");
    private String ElementalVal;

    public ElementalItem(String Inputname, String Inputdescription, String ElementalValue) {

        name = Inputname;
        description = Inputdescription;

        ElementalVal = ElementalValue;

    }

    public PersistentDataContainer editPersistentData(PersistentDataContainer container) {
        container.set(key, PersistentDataType.STRING, ElementalVal);
        return container;
    }

}
