package org.example.bounty;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DatabaseManager {
    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private Connection connection;
    private final Map<UUID, Double> bountyCache = new HashMap<>();
    private final Map<UUID, Double> killBountyCache = new HashMap<>();
    private final Map<UUID, Double> deathBountyCache = new HashMap<>();

    public DatabaseManager(JavaPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public void init() {
        String dbType = configManager.getDatabaseType();
        if (dbType.equalsIgnoreCase("sqlite")) {
            try {
                connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + "/bounty.db");
                createTables();
            } catch (SQLException e) {
                plugin.getLogger().severe("Не удалось подключиться к SQLite: " + e.getMessage());
            }
        } else {
            plugin.getLogger().severe("Неподдерживаемый тип базы данных: " + dbType);
        }
    }

    private void createTables() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS bounty (" +
                "uuid TEXT PRIMARY KEY," +
                "bounty DOUBLE NOT NULL DEFAULT 0," +
                "kill_bounty DOUBLE NOT NULL DEFAULT 0," +
                "death_bounty DOUBLE NOT NULL DEFAULT 0)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.execute();
        }
    }

    public double getBounty(UUID playerUUID) {
        if (bountyCache.containsKey(playerUUID)) {
            return bountyCache.get(playerUUID);
        }
        try (PreparedStatement stmt = connection.prepareStatement("SELECT bounty FROM bounty WHERE uuid = ?")) {
            stmt.setString(1, playerUUID.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double bounty = rs.getDouble("bounty");
                bountyCache.put(playerUUID, bounty);
                return bounty;
            } else {
                setBounty(playerUUID, configManager.getDefaultBounty());
                return configManager.getDefaultBounty();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка при получении баунти: " + e.getMessage());
            return 0;
        }
    }

    public void setBounty(UUID playerUUID, double bounty) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT OR REPLACE INTO bounty (uuid, bounty, kill_bounty, death_bounty) " +
                        "VALUES (?, ?, COALESCE((SELECT kill_bounty FROM bounty WHERE uuid = ?), 0), " +
                        "COALESCE((SELECT death_bounty FROM bounty WHERE uuid = ?), 0))")) {
            stmt.setString(1, playerUUID.toString());
            stmt.setDouble(2, bounty);
            stmt.setString(3, playerUUID.toString());
            stmt.setString(4, playerUUID.toString());
            stmt.executeUpdate();
            bountyCache.put(playerUUID, bounty);
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка при установке баунти: " + e.getMessage());
        }
    }

    public double getKillBounty(UUID playerUUID) {
        if (killBountyCache.containsKey(playerUUID)) {
            return killBountyCache.get(playerUUID);
        }
        try (PreparedStatement stmt = connection.prepareStatement("SELECT kill_bounty FROM bounty WHERE uuid = ?")) {
            stmt.setString(1, playerUUID.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double killBounty = rs.getDouble("kill_bounty");
                killBountyCache.put(playerUUID, killBounty);
                return killBounty;
            }
            return 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка при получении kill_bounty: " + e.getMessage());
            return 0;
        }
    }

    public void setKillBounty(UUID playerUUID, double killBounty) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT OR REPLACE INTO bounty (uuid, bounty, kill_bounty, death_bounty) " +
                        "VALUES (?, COALESCE((SELECT bounty FROM bounty WHERE uuid = ?), ?), ?, " +
                        "COALESCE((SELECT death_bounty FROM bounty WHERE uuid = ?), 0))")) {
            stmt.setString(1, playerUUID.toString());
            stmt.setString(2, playerUUID.toString());
            stmt.setDouble(3, getBounty(playerUUID));
            stmt.setDouble(4, killBounty);
            stmt.setString(5, playerUUID.toString());
            stmt.executeUpdate();
            killBountyCache.put(playerUUID, killBounty);
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка при установке kill_bounty: " + e.getMessage());
        }
    }

    public double getDeathBounty(UUID playerUUID) {
        if (deathBountyCache.containsKey(playerUUID)) {
            return deathBountyCache.get(playerUUID);
        }
        try (PreparedStatement stmt = connection.prepareStatement("SELECT death_bounty FROM bounty WHERE uuid = ?")) {
            stmt.setString(1, playerUUID.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double deathBounty = rs.getDouble("death_bounty");
                deathBountyCache.put(playerUUID, deathBounty);
                return deathBounty;
            }
            return 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка при получении death_bounty: " + e.getMessage());
            return 0;
        }
    }

    public void setDeathBounty(UUID playerUUID, double deathBounty) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT OR REPLACE INTO bounty (uuid, bounty, kill_bounty, death_bounty) " +
                        "VALUES (?, COALESCE((SELECT bounty FROM bounty WHERE uuid = ?), ?), " +
                        "COALESCE((SELECT kill_bounty FROM bounty WHERE uuid = ?), 0), ?)")) {
            stmt.setString(1, playerUUID.toString());
            stmt.setString(2, playerUUID.toString());
            stmt.setDouble(3, getBounty(playerUUID));
            stmt.setString(4, playerUUID.toString());
            stmt.setDouble(5, deathBounty);
            stmt.executeUpdate();
            deathBountyCache.put(playerUUID, deathBounty);
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка при установке death_bounty: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка при закрытии базы данных: " + e.getMessage());
        }
    }
}