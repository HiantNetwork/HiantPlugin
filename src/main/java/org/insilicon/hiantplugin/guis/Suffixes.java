package org.insilicon.hiantplugin.guis;

import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import net.cybercake.cyberapi.spigot.CyberAPI;
import net.cybercake.cyberapi.spigot.chat.Log;
import net.cybercake.cyberapi.spigot.chat.UChat;
import net.cybercake.cyberapi.spigot.items.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.insilicon.hiantplugin.Config;
import org.insilicon.hiantplugin.HiantPlugin;
import org.insilicon.hiantplugin.Utils.HiantUtils;
import org.insilicon.hiantplugin.configuration.SuffixConfig;

import java.util.Map;

import static org.insilicon.hiantplugin.Utils.HiantUtils.getDoubleChest;

@SuppressWarnings({"ConstantConditions"})
public class Suffixes extends FastInv {

    private final Player player;
    private final int startingItem;
    private final int currentPage;
    private final ItemBuilder NEXT_PAGE = new ItemBuilder(Material.GREEN_WOOL).name(UChat.chat("&aNext page"));
    private final ItemBuilder LAST_PAGE = new ItemBuilder(Material.RED_WOOL).name(UChat.chat("&cPrevious page"));
    private final ItemStack CLOSE_GUI = new ItemBuilder(Material.BARRIER).name(UChat.chat("&cClose")).build();
    private final ItemStack RESET_PREFIX = new ItemBuilder(Material.REDSTONE_TORCH).name(UChat.chat("&4Reset suffix")).build();

    public Suffixes(Player player, int startingItem, int currentPage) {
        super(getDoubleChest(), "Suffixes (" + currentPage + ")");

        this.player = player;
        this.startingItem = startingItem;
        this.currentPage = currentPage;

        player.updateInventory();

        // Add some blocks to the borders

        ItemStack BORDER_ITEM = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name(" ").build();
        setItems(getBorders(), BORDER_ITEM);
        setItem(49, CLOSE_GUI);
        setItem(45, RESET_PREFIX);

        ConfigurationSection section = SuffixConfig.getFileConfig().getConfigurationSection("suffixes.list");
        if (section == null) return;
        int numOfItems = section.getKeys(false).size();
        if (numOfItems >= (startingItem + 28)) {
            // make next page button
            setItem(52, NEXT_PAGE.name(UChat.chat("&aNext Page (" + (currentPage + 1) + ")")).build());
        }
        if (startingItem - 28 > 0) {
            setItem(46, LAST_PAGE.name(UChat.chat("&cLast Page (" + (currentPage - 1) + ")")).build());
        }

        int currentSlot = 0;
        int currentItem = 1;

        for (Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
            if (currentSlot >= (44)) {
                // next page
                return;
            }

            if (getInventory().getItem(currentSlot) != null && (getInventory().getItem(currentSlot).getType() == BORDER_ITEM.getType())) {
                do {
                    currentSlot++;
                } while (getInventory().getItem(currentSlot) != null && (getInventory().getItem(currentSlot).getType() == BORDER_ITEM.getType()));
            }

            if (currentItem < startingItem) {
                currentItem++;
                continue;
            }

            currentItem++;

            String key = entry.getKey();
            String value = (String) entry.getValue();

            if (!player.hasPermission("hiant.suffix." + HiantUtils.stripMiniMessageColor(key.toLowerCase()))) {
                ItemStack itemStack = new ItemCreator.ItemBuilder(Material.BARRIER)
                        .itemTextFormatter(ItemCreator.ItemTextFormatter.MINIMESSAGE)
                        .name(HiantUtils.convertLegacyColors(key))
                        .lore(HiantUtils.convertLegacyColors(
                                        value.replace("%player%", player.getName())),
                                "\n",
                                HiantUtils.convertLegacyColors("&cUnlock this suffix with &b/store")
                        ).build();

                ItemMeta noPermsMeta = itemStack.getItemMeta();
                noPermsMeta.getPersistentDataContainer().set(new NamespacedKey(HiantPlugin.getInstance(), "key"), PersistentDataType.STRING, "noPermissionsAllowedForTag");
                noPermsMeta.getPersistentDataContainer().set(new NamespacedKey(HiantPlugin.getInstance(), "value"), PersistentDataType.STRING, "noPermissionsAllowedForTag");
                itemStack.setItemMeta(noPermsMeta);

                setItem(currentSlot, itemStack);
            } else {
                ItemStack itemStack = new ItemCreator.ItemBuilder(Material.NAME_TAG)
                        .itemTextFormatter(ItemCreator.ItemTextFormatter.MINIMESSAGE)
                        .name(HiantUtils.convertLegacyColors(key))
                        .lore(HiantUtils.convertLegacyColors(
                                        value.replace("%player%", player.getName())
                                                .replace("%playerdisplay%", HiantUtils.getDisplayNameIfExists(player))
                                                .replace("%playerprefix%", HiantUtils.getPrefixIfExists(player))
                                                .replace("%suffix%", key)
                                ),
                                "\n",
                                HiantUtils.convertLegacyColors("Click to set as your suffix!")
                        ).build();

                ItemMeta meta = itemStack.getItemMeta();
                meta.getPersistentDataContainer().set(new NamespacedKey(HiantPlugin.getInstance(), "key"), PersistentDataType.STRING, key);
                meta.getPersistentDataContainer().set(new NamespacedKey(HiantPlugin.getInstance(), "value"), PersistentDataType.STRING, value);

                itemStack.setItemMeta(meta);

                setItem(currentSlot, itemStack);
            }

            currentSlot++;

        }
    }

    private void updateInventory(InventoryClickEvent event) {
        event.setCancelled(true);
        player.setItemOnCursor(new ItemStack(Material.AIR));
        player.updateInventory();

        Bukkit.getScheduler().runTaskLater(HiantPlugin.getInstance(), player::updateInventory, 5);
        Bukkit.getScheduler().runTaskLater(HiantPlugin.getInstance(), player::updateInventory, 10);
    }

    @Override
    public void onClick(InventoryClickEvent event) {

        updateInventory(event);

        int currentSlot = event.getSlot();
        if (getInventory().getItem(currentSlot) != null && (getInventory().getItem(currentSlot).getType() == NEXT_PAGE.build().getType())) {
            new Suffixes(player, startingItem + 28, currentPage + 1).open(player);
            return;
        }

        if (getInventory().getItem(currentSlot) != null && (getInventory().getItem(currentSlot).getType() == LAST_PAGE.build().getType())) {
            new Suffixes(player, startingItem - 28, currentPage - 1).open(player);
            return;
        }

        if (getInventory().getItem(currentSlot) != null && (getInventory().getItem(currentSlot).equals(CLOSE_GUI))) {
            getInventory().close();
            return;
        }

        if (getInventory().getItem(currentSlot) != null && (getInventory().getItem(currentSlot).equals(RESET_PREFIX))) {
            HiantUtils.removePlayerSuffix(player, 1);
            player.sendMessage(HiantUtils.coloredMiniMessage(Config.getPrefix() + "&eYou reset your suffix"));
            getInventory().close();
            return;
        }

        if (getInventory().getItem(currentSlot) != null && (getInventory().getItem(currentSlot).getType().equals(Material.BARRIER))) {
            CyberAPI.getInstance().playSound(player, Sound.ENTITY_VILLAGER_NO, 1, 1);
            player.sendMessage(UChat.component(Config.getErrorPrefix() + "&cUnlock that suffix with &b/store"));
            return;
        } // For items players haven't unlocked

        if (getInventory().getItem(currentSlot) != null && getInventory().getItem(currentSlot).getType() == Material.NAME_TAG) {
            ItemMeta meta = getInventory().getItem(currentSlot).getItemMeta();
            String key = meta.getPersistentDataContainer().get(new NamespacedKey(HiantPlugin.getInstance(), "key"), PersistentDataType.STRING);
            String value = meta.getPersistentDataContainer().get(new NamespacedKey(HiantPlugin.getInstance(), "value"), PersistentDataType.STRING);

            if (key.equals("noPermissionsAllowedForTag") || value.equals("noPermissionsAllowedForTag")) {
                Log.info("no perms, returning");
                return;
            }

//            JSONObject playerCache = new DBCacheHandler(player).getPlayerData();
//
//            boolean isSelected = playerCache.has("serverjoinmessage") && playerCache.getString("serverjoinmessage").split("::")[0].equals(key);

//            if (!isSelected) {
//                playerCache.put("serverjoinmessage", (key + "::" + value));
//                CyberAPI.getInstance().playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
//                player.sendMessage(CommonUtils.coloredMiniMessage(Config.getPrefix() + "&7You selected " + key));
//            } else {
//                playerCache.remove("serverjoinmessage");
//                CyberAPI.getInstance().playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
//                player.sendMessage(CommonUtils.coloredMiniMessage(Config.getWarnPrefix() + "&7You unselected " + key));
//            }

            HiantUtils.setPlayerSuffix(player, key, 1);
            player.sendMessage(HiantUtils.coloredMiniMessage(Config.getPrefix() + "&eYou selected suffix &6" + key));

            getInventory().close();
//            new Suffixes(player, startingItem, currentPage).open(player);
        }
    }
}