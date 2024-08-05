package xyz.prorickey.hiantplugin.database;

import net.cybercake.cyberapi.spigot.basic.BetterStackTraces;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.insilicon.hiantplugin.HiantPlugin;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class Database {

    private Connection connection;

    public Database() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mariadb://" +
                    HiantPlugin.getConf().getString("database.host") +
                    ":" +
                    HiantPlugin.getConf().getString("database.port") +
                    "/" + HiantPlugin.getConf().getString("database.database"),
                    HiantPlugin.getConf().getString("database.username"),
                    HiantPlugin.getConf().getString("database.password"));

            PreparedStatement createDiscord = connection.prepareStatement("CREATE TABLE IF NOT EXISTS discord (" +
                    "minecraft_uuid VARCHAR(255) PRIMARY KEY, " +
                    "discord_id BIGINT)");

            createDiscord.execute();

            PreparedStatement createEnderChest = connection.prepareStatement("CREATE TABLE IF NOT EXISTS ender_chests (" +
                    "uuid VARCHAR(255), " +
                    "slot TINYINT, " +
                    "item BLOB, " +
                    "PRIMARY KEY (uuid, slot))");

            createEnderChest.execute();
        } catch (Exception e) {
            BetterStackTraces.print(e);
        }
    }

    /**
     * Caches the data of a player
     * @param uuid The UUID of the player
     **/
    public void cacheData(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(HiantPlugin.getPlugin(), () -> {
            try {
                PreparedStatement linkStmt = connection.prepareStatement("SELECT * FROM discord WHERE minecraft_uuid = ?");
                linkStmt.setString(1, uuid.toString());
                ResultSet set = linkStmt.executeQuery();

                if (set.next()) {
                    if(set.getLong("discord_id") == 0) linkedBefore.add(uuid);
                    else cachedLinkedAccounts.put(uuid, new LinkedAccount(set.getLong("discord_id"), uuid.toString()));
                }

                PreparedStatement enderChestStmt = connection.prepareStatement("SELECT * FROM ender_chests WHERE uuid = ?");
                enderChestStmt.setString(1, uuid.toString());
                ResultSet enderChestSet = enderChestStmt.executeQuery();
                Map<Integer, ItemStack> enderChest = new HashMap<>();
                while (enderChestSet.next())
                    enderChest.put(enderChestSet.getInt("slot"), enderChestSet.getBytes("item") == null ? new ItemStack(Material.AIR) : ItemStack.deserializeBytes(enderChestSet.getBytes("item")));
                if(enderChest.isEmpty()) for(int i = 0; i < 27; i++) enderChest.put(i, new ItemStack(Material.AIR));
                cachedEnderChests.put(uuid, enderChest);
            } catch (Exception e) {
                BetterStackTraces.print(e);
            }
        });
    }

    /**
     * Uncaches the data of a player
     * @param uuid The UUID of the player
     */
    public void unCacheData(UUID uuid) {
        cachedLinkedAccounts.remove(uuid);
        cachedEnderChests.remove(uuid);
    }

    private final List<UUID> linkedBefore = new ArrayList<>();
    private final Map<UUID, LinkedAccount> cachedLinkedAccounts = new HashMap<>();
    private final Map<UUID, Map<Integer, ItemStack>> cachedEnderChests = new HashMap<>();

    /**
     * Loads the ender chest of a player
     * @param uuid The UUID of the player
     * @return The ender chest of the player
     */
    public Map<Integer, ItemStack> getCachedEnderChest(UUID uuid) {
        return cachedEnderChests.get(uuid);
    }

    /**
     * Upgrades the ender chest of a player
     * @param uuid The UUID of the player
     */
    public void upgradeEnderChest(UUID uuid) { // TODO: Probably some of the worst code I've written in my life... will need to update
        Bukkit.getScheduler().runTaskAsynchronously(HiantPlugin.getPlugin(), () -> {
            try {
                PreparedStatement enderChestStmt = connection.prepareStatement("SELECT * FROM ender_chests WHERE uuid = ?");
                enderChestStmt.setString(1, uuid.toString());
                ResultSet enderChestSet = enderChestStmt.executeQuery();
                Map<Integer, ItemStack> enderChest = new HashMap<>();
                while (enderChestSet.next())
                    enderChest.put(enderChestSet.getInt("slot"), ItemStack.deserializeBytes(enderChestSet.getBytes("item")));
                for(int i = 0; i < 54; i++) if(enderChest.get(i) == null) enderChest.put(i, new ItemStack(Material.AIR));

                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO ender_chests (uuid, slot, item) VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE item = VALUES(item)");

                for (Map.Entry<Integer, ItemStack> entry : enderChest.entrySet()) {
                    preparedStatement.setString(1, uuid.toString());
                    preparedStatement.setInt(2, entry.getKey());
                    preparedStatement.setBytes(3, entry.getValue().getType() == Material.AIR ? null : entry.getValue().serializeAsBytes());
                    preparedStatement.addBatch();
                }

                preparedStatement.executeBatch();
            } catch (Exception e) {
                BetterStackTraces.print(e);
            }
        });
    }

    /**
     * Saves the ender chest into the database of the player
     * @param uuid The UUID of the player
     * @param inventory The inventory to load the ender chest into
     */
    public void saveEnderChest(UUID uuid, Inventory inventory) {
        Map<Integer, ItemStack> items = new HashMap<>();
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null) items.put(i, item);
        }
        cachedEnderChests.put(uuid, items);
        Bukkit.getScheduler().runTaskAsynchronously(HiantPlugin.getPlugin(), () -> {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO ender_chests (uuid, slot, item) VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE item = VALUES(item)");

                for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
                    preparedStatement.setString(1, uuid.toString());
                    preparedStatement.setInt(2, entry.getKey());
                    preparedStatement.setBytes(3, entry.getValue().serializeAsBytes());
                    preparedStatement.addBatch();
                }

                preparedStatement.executeBatch();
            } catch (Exception e) {
                BetterStackTraces.print(e);
            }
        });
    }

    /**
     * Represents a linked account
     * <p>
     * ReadOnly object
     */
    public static class LinkedAccount {
        private final Long discordId;
        private final String minecraftUUID;

        public LinkedAccount(Long discordId, String minecraftUUID) {
            this.discordId = discordId;
            this.minecraftUUID = minecraftUUID;
        }

        public Long getDiscordId() { return discordId; }
        public String getMinecraftUUID() { return minecraftUUID; }
    }

    public Boolean linkedBefore(UUID uuid) {
        return linkedBefore.contains(uuid);
    }

    /**
     * Gets the linked account of a player
     * @param uuid The UUID of the player
     * @return The linked account of the player, or null if the player is not linked
     */
    public @Nullable LinkedAccount getLinkedAccount(UUID uuid) {
        return cachedLinkedAccounts.get(uuid);
    }

    public @Nullable LinkedAccount getLinkedAccount(Long discordId) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM discord WHERE discord_id = ?");
            preparedStatement.setLong(1, discordId);
            ResultSet set = preparedStatement.executeQuery();

            if (set.next()) return new LinkedAccount(discordId, preparedStatement.getResultSet().getString("minecraft_uuid"));
        } catch (Exception e) {
            BetterStackTraces.print(e);
        }
        return null;
    }

    /**
     * Links a Discord account to a Minecraft account
     * @param discordId Discord account id
     * @param minecraftUUID Minecraft account UUID
     */
    public void linkAccount(Long discordId, String minecraftUUID) {
        cachedLinkedAccounts.put(UUID.fromString(minecraftUUID), new LinkedAccount(discordId, minecraftUUID));
        Bukkit.getScheduler().runTaskAsynchronously(HiantPlugin.getPlugin(), () -> {
            try {
                if(linkedBefore.contains(UUID.fromString(minecraftUUID))) {
                    PreparedStatement preparedStatement = connection.prepareStatement("UPDATE discord SET discord_id = ? WHERE minecraft_uuid = ?");
                    preparedStatement.setLong(1, discordId);
                    preparedStatement.setString(2, minecraftUUID);
                    preparedStatement.execute();
                    linkedBefore.remove(UUID.fromString(minecraftUUID));
                } else {
                    PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO discord (discord_id, minecraft_uuid) VALUES (?, ?)");
                    preparedStatement.setLong(1, discordId);
                    preparedStatement.setString(2, minecraftUUID);
                    preparedStatement.execute();
                }
            } catch (Exception e) {
                BetterStackTraces.print(e);
            }
        });
    }

    /**
     * Unlinks a Discord account from a Minecraft account
     * @param discordId The Discord account id
     */
    public void unlinkAccount(Long discordId) {
        LinkedAccount la = cachedLinkedAccounts.entrySet().stream().filter(entry -> entry.getValue().getDiscordId().equals(discordId)).findFirst().orElse(null).getValue();
        cachedLinkedAccounts.entrySet().removeIf(entry -> entry.getValue().getDiscordId().equals(discordId));
        linkedBefore.add(UUID.fromString(la.getMinecraftUUID()));
        Bukkit.getScheduler().runTaskAsynchronously(HiantPlugin.getPlugin(), () -> {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE discord SET discord_id = NULL WHERE discord_id = ?");
                preparedStatement.setLong(1, discordId);
                preparedStatement.execute();
            } catch (Exception e) {
                BetterStackTraces.print(e);
            }
        });
    }

    /**
     * Unlinks a Minecraft account from a Discord account
     * @param minecraftUUID The Minecraft account UUID
     */
    public void unlinkAccount(UUID minecraftUUID) {
        cachedLinkedAccounts.remove(minecraftUUID);
        linkedBefore.add(minecraftUUID);
        Bukkit.getScheduler().runTaskAsynchronously(HiantPlugin.getPlugin(), () -> {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE discord SET discord_id = NULL WHERE minecraft_uuid = ?");
                preparedStatement.setString(1, minecraftUUID.toString());
                preparedStatement.execute();
            } catch (Exception e) {
                BetterStackTraces.print(e);
            }
        });
    }

    /**
     * Unlinks a Discord account from a Minecraft account
     * @return The connection to the database
     */
    public Connection getConnection() {
        return connection;
    }

}
