package org.example.bounty;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class BountyManager implements Listener {
    private final DatabaseManager databaseManager;
    private final ConfigManager configManager;
    private final BountyPlugin plugin;

    public BountyManager(DatabaseManager databaseManager, ConfigManager configManager) {
        this.databaseManager = databaseManager;
        this.configManager = configManager;
        this.plugin = (BountyPlugin) databaseManager.getPlugin();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer != null && !killer.equals(victim)) {
            double killerBounty = databaseManager.getBounty(killer.getUniqueId());
            double killBountyPercent = databaseManager.getKillBounty(killer.getUniqueId());
            if (killBountyPercent == 0.0) killBountyPercent = configManager.getKillBounty();
            double totalIncreasePercent = configManager.getIncreasePercent() + killBountyPercent;
            double increase = killerBounty * (totalIncreasePercent / 100.0);
            databaseManager.setBounty(killer.getUniqueId(), killerBounty + increase);
            String message = configManager.getKillMessage()
                    .replace("%victim%", victim.getName())
                    .replace("%kill_amount%", String.format("%.2f", increase));
            if (plugin.isPlaceholderAPIEnabled()) {
                message = PlaceholderAPI.setPlaceholders(killer, message);
            } else {
                message = message.replace("%bounty%", String.format("%.2f", killerBounty + increase));
            }
            killer.sendMessage(ChatColor.translateAlternateColorCodes('&', message));

            double victimBounty = databaseManager.getBounty(victim.getUniqueId());
            double deathBountyPercent = databaseManager.getDeathBounty(victim.getUniqueId());
            if (deathBountyPercent == 0.0) deathBountyPercent = configManager.getDeathBounty();
            double totalDecreasePercent = configManager.getDecreasePercent() + deathBountyPercent;
            double decrease = victimBounty * (totalDecreasePercent / 100.0);
            databaseManager.setBounty(victim.getUniqueId(), Math.max(0, victimBounty - decrease));
            message = configManager.getDeathMessage()
                    .replace("%death_amount%", String.format("%.2f", decrease));
            if (plugin.isPlaceholderAPIEnabled()) {
                message = PlaceholderAPI.setPlaceholders(victim, message);
            } else {
                message = message.replace("%bounty%", String.format("%.2f", Math.max(0, victimBounty - decrease)));
            }
            victim.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (!plugin.isPlaceholderAPIEnabled()) return;
        Player player = event.getPlayer();
        String format = event.getFormat();
        format = PlaceholderAPI.setPlaceholders(player, format);
        event.setFormat(format);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!plugin.isPlaceholderAPIEnabled()) return;
        Player player = event.getPlayer();
        String tabName = PlaceholderAPI.setPlaceholders(player, "%bounty%");
        player.setPlayerListName(ChatColor.translateAlternateColorCodes('&', tabName));
    }

    public double getBounty(UUID playerUUID) {
        return databaseManager.getBounty(playerUUID);
    }

    public void setBounty(UUID playerUUID, double bounty) {
        databaseManager.setBounty(playerUUID, Math.max(0, bounty));
    }

    public void applyBounty(UUID playerUUID) {
        setBounty(playerUUID, configManager.getDefaultBounty());
    }

    public void addBounty(UUID playerUUID, double amount) {
        setBounty(playerUUID, getBounty(playerUUID) + amount);
    }

    public void removeBounty(UUID playerUUID, double amount) {
        setBounty(playerUUID, getBounty(playerUUID) - amount);
    }

    public double getPlayerKillBounty(UUID playerUUID) {
        return databaseManager.getKillBounty(playerUUID);
    }

    public void setPlayerKillBounty(UUID playerUUID, double killBounty) {
        databaseManager.setKillBounty(playerUUID, killBounty);
    }

    public void applyKillBounty(UUID playerUUID) {
        double killBountyPercent = databaseManager.getKillBounty(playerUUID);
        if (killBountyPercent == 0.0) killBountyPercent = configManager.getKillBounty();
        double totalIncreasePercent = configManager.getIncreasePercent() + killBountyPercent;
        double bounty = getBounty(playerUUID);
        addBounty(playerUUID, bounty * (totalIncreasePercent / 100.0));
    }

    public double getPlayerDeathBounty(UUID playerUUID) {
        return databaseManager.getDeathBounty(playerUUID);
    }

    public void setPlayerDeathBounty(UUID playerUUID, double deathBounty) {
        databaseManager.setDeathBounty(playerUUID, deathBounty);
    }

    public void applyDeathBounty(UUID playerUUID) {
        double deathBountyPercent = databaseManager.getDeathBounty(playerUUID);
        if (deathBountyPercent == 0.0) deathBountyPercent = configManager.getDeathBounty();
        double totalDecreasePercent = configManager.getDecreasePercent() + deathBountyPercent;
        double bounty = getBounty(playerUUID);
        removeBounty(playerUUID, bounty * (totalDecreasePercent / 100.0));
    }

    public double getGlobalBounty() {
        return configManager.getDefaultBounty();
    }

    public void setGlobalBounty(double amount) {
        configManager.setDefaultBounty(amount);
    }

    public double getGlobalKillBounty() {
        return configManager.getKillBounty();
    }

    public void setGlobalKillBounty(double amount) {
        configManager.setKillBounty(amount);
    }

    public double getGlobalDeathBounty() {
        return configManager.getDeathBounty();
    }

    public void setGlobalDeathBounty(double amount) {
        configManager.setDeathBounty(amount);
    }
}