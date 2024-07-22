package xyz.prorickey.hiantplugin.database;

import net.cybercake.cyberapi.spigot.basic.BetterStackTraces;
import org.bukkit.Bukkit;
import org.insilicon.hiantplugin.HiantPlugin;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS discord (discord_id BIGINT PRIMARY KEY, minecraft_uuid VARCHAR(255))");
            preparedStatement.execute();
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
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM discord WHERE minecraft_uuid = ?");
                preparedStatement.setString(1, uuid.toString());
                ResultSet set = preparedStatement.executeQuery();

                if (set.next()) {
                    LinkedAccount linkedAccount = new LinkedAccount(preparedStatement.getResultSet().getLong("discord_id"), uuid.toString());
                    cachedLinkedAccounts.put(uuid, linkedAccount);
                }
            } catch (Exception e) {
                BetterStackTraces.print(e);
            }
        });
    }

    private final Map<UUID, LinkedAccount> cachedLinkedAccounts = new HashMap<>();

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
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO discord (discord_id, minecraft_uuid) VALUES (?, ?)");
                preparedStatement.setLong(1, discordId);
                preparedStatement.setString(2, minecraftUUID);
                preparedStatement.execute();
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
        cachedLinkedAccounts.entrySet().removeIf(entry -> entry.getValue().getDiscordId().equals(discordId));
        Bukkit.getScheduler().runTaskAsynchronously(HiantPlugin.getPlugin(), () -> {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM discord WHERE discord_id = ?");
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
        Bukkit.getScheduler().runTaskAsynchronously(HiantPlugin.getPlugin(), () -> {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM discord WHERE minecraft_uuid = ?");
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
