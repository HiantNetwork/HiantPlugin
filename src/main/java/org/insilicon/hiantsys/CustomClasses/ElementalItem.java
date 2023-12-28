package org.insilicon.hiantsys.CustomClasses;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.insilicon.hiantsys.hiantsys;

public class ElementalItem {
    private String name;
    private String description;
    public NamespacedKey key = new NamespacedKey(hiantsys.getPlugin(hiantsys.class), "ElementalItem");
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
