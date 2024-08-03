package xyz.prorickey.hiantplugin;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.insilicon.hiantplugin.HiantPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EnderChestHandler implements Listener {

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if(!event.getInventory().getType().equals(InventoryType.ENDER_CHEST)) return;
        HumanEntity player = event.getPlayer();
        event.setCancelled(true);
        player.openInventory(getPlayerEnderChest(player));
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if(!(event.getInventory().getHolder() instanceof HiantEnderChestHolder)) return;
        savePlayerEnderChest(event.getPlayer().getUniqueId(), event.getInventory());
    }

    @EventHandler
    public void onInventoryInteract(InventoryInteractEvent event) {
        if(!(event.getInventory().getHolder() instanceof HiantEnderChestHolder)) return;
        savePlayerEnderChest(event.getWhoClicked().getUniqueId(), event.getInventory());
    }

    private static Map<UUID, Inventory> enderChests = new HashMap<>();

    public static void upgradePlayerEnderChest(UUID uuid) {
        if(Bukkit.getOnlinePlayers().stream().filter(p -> p.getUniqueId().equals(uuid)).count() == 1) {
            Inventory ec = getPlayerEnderChest((Player) Bukkit.getOnlinePlayers().stream().filter(p -> p.getUniqueId().equals(uuid)).toArray()[0]);
            int rows = ec.getSize() / 9;
            if(rows >= 6) return;

            Inventory newEC = Bukkit.createInventory(new HiantEnderChestHolder(), 54, MiniMessage.miniMessage().deserialize("<gradient:#2ba8ed:#5bb0df><b>Ender Chest</b>"));
            for(int i = 0; i < ec.getSize(); i++) {
                newEC.setItem(i, ec.getItem(i));
            }

            savePlayerEnderChest(uuid, newEC);
        } else HiantPlugin.getDatabase().upgradeEnderChest(uuid);
    }

    public static Inventory getPlayerEnderChest(HumanEntity player) {
        if(!enderChests.containsKey(player.getUniqueId())) {
            Map<Integer, ItemStack> enderChestItems = HiantPlugin.getDatabase().getCachedEnderChest(player.getUniqueId());
            int rows = enderChestItems.size() / 9;

            String title = "<gradient:#2ba8ed:#5bb0df><b>Ender Chest</b>";
            if(rows < 6) title += " <yellow>(/buy)";
            Inventory ec = Bukkit.createInventory(new HiantEnderChestHolder(), rows*9, MiniMessage.miniMessage().deserialize(title));
            enderChestItems.forEach(ec::setItem);
            enderChests.put(player.getUniqueId(), ec);
        }

        return enderChests.get(player.getUniqueId());
    }

    private static void savePlayerEnderChest(UUID uuid, Inventory inventory) {
        enderChests.put(uuid, inventory);
        HiantPlugin.getDatabase().saveEnderChest(uuid, inventory);
    }

    public static class HiantEnderChestHolder implements InventoryHolder {

        public HiantEnderChestHolder() {}

        @Override
        public @NotNull Inventory getInventory() {
            return null;
        }
    }

}
