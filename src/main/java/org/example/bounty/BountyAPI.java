package org.example.bounty;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class BountyAPI {
    private final BountyPlugin plugin;

    public BountyAPI(Plugin plugin) {
        this.plugin = (BountyPlugin) plugin;
    }

    public double getBounty(UUID playerUUID) {
        return plugin.getBountyManager().getBounty(playerUUID);
    }

    public void setBounty(UUID playerUUID, double bounty) {
        plugin.getBountyManager().setBounty(playerUUID, bounty);
    }

    public void applyBounty(UUID playerUUID) {
        plugin.getBountyManager().applyBounty(playerUUID);
    }

    public void addBounty(UUID playerUUID, double amount) {
        plugin.getBountyManager().addBounty(playerUUID, amount);
    }

    public void removeBounty(UUID playerUUID, double amount) {
        plugin.getBountyManager().removeBounty(playerUUID, amount);
    }

    public double getGlobalBounty() {
        return plugin.getBountyManager().getGlobalBounty();
    }

    public void setGlobalBounty(double amount) {
        plugin.getBountyManager().setGlobalBounty(amount);
    }

    public double getPlayerKillBounty(UUID playerUUID) {
        return plugin.getBountyManager().getPlayerKillBounty(playerUUID);
    }

    public void setPlayerKillBounty(UUID playerUUID, double killBounty) {
        plugin.getBountyManager().setPlayerKillBounty(playerUUID, killBounty);
    }

    public void applyKillBounty(UUID playerUUID) {
        plugin.getBountyManager().applyKillBounty(playerUUID);
    }

    public double getGlobalKillBounty() {
        return plugin.getBountyManager().getGlobalKillBounty();
    }

    public void setGlobalKillBounty(double amount) {
        plugin.getBountyManager().setGlobalKillBounty(amount);
    }

    public double getPlayerDeathBounty(UUID playerUUID) {
        return plugin.getBountyManager().getPlayerDeathBounty(playerUUID);
    }

    public void setPlayerDeathBounty(UUID playerUUID, double deathBounty) {
        plugin.getBountyManager().setPlayerDeathBounty(playerUUID, deathBounty);
    }

    public void applyDeathBounty(UUID playerUUID) {
        plugin.getBountyManager().applyDeathBounty(playerUUID);
    }

    public double getGlobalDeathBounty() {
        return plugin.getBountyManager().getGlobalDeathBounty();
    }

    public void setGlobalDeathBounty(double amount) {
        plugin.getBountyManager().setGlobalDeathBounty(amount);
    }

    public String replacePlaceholders(Player player, String message) {
        if (plugin.isPlaceholderAPIEnabled() && player != null) {
            return PlaceholderAPI.setPlaceholders(player, message);
        }
        return message.replace("%bounty%", String.format("%.2f", getBounty(player.getUniqueId())));
    }
}