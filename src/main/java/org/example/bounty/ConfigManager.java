package org.example.bounty;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {
    private final JavaPlugin plugin;
    private FileConfiguration config;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public double getDefaultBounty() {
        return config.getDouble("bounty.default-bounty", 0);
    }

    public void setDefaultBounty(double amount) {
        config.set("bounty.default-bounty", amount);
        plugin.saveConfig();
    }

    public double getIncreasePercent() {
        return config.getDouble("bounty.increase-percent", 10);
    }

    public double getDecreasePercent() {
        return config.getDouble("bounty.decrease-percent", 20);
    }

    public double getKillBounty() {
        return config.getDouble("bounty.kill-bounty", 50);
    }

    public void setKillBounty(double amount) {
        config.set("bounty.kill-bounty", amount);
        plugin.saveConfig();
    }

    public double getDeathBounty() {
        return config.getDouble("bounty.death-bounty", 30);
    }

    public void setDeathBounty(double amount) {
        config.set("bounty.death-bounty", amount);
        plugin.saveConfig();
    }

    public String getKillMessage() {
        return config.getString("messages.kill-message", "&aВы убили %victim% и получили +%kill_amount% баунти! Ваш баунти: %bounty%");
    }

    public String getDeathMessage() {
        return config.getString("messages.death-message", "&cВы умерли и потеряли %death_amount% баунти! Ваш баунти: %bounty%");
    }

    public String getDatabaseType() {
        return config.getString("database.type", "sqlite");
    }
}