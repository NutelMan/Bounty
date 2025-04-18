package org.example.bounty;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BountyPlaceholder extends PlaceholderExpansion {
    private final BountyPlugin plugin;

    public BountyPlaceholder(BountyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "bounty";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Sausage";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) return null;
        if (identifier.isEmpty()) {
            return String.format("%.2f", plugin.getBountyManager().getBounty(player.getUniqueId()));
        }
        return null;
    }
}