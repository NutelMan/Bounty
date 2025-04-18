package org.example.bounty;

import org.bukkit.plugin.java.JavaPlugin;

public class BountyPlugin extends JavaPlugin {
    private BountyManager bountyManager;
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private BountyPlaceholder placeholder;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        configManager = new ConfigManager(this);
        databaseManager = new DatabaseManager(this, configManager);
        databaseManager.init();
        bountyManager = new BountyManager(databaseManager, configManager);
        getServer().getPluginManager().registerEvents(bountyManager, this);
        getCommand("bounty").setExecutor(new BountyCommand(bountyManager));
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            getLogger().info("PlaceholderAPI найден, регистрируем плейсхолдер...");
            placeholder = new BountyPlaceholder(this);
            if (placeholder.register()) {
                getLogger().info("Плейсхолдер %bounty% успешно зарегистрирован!");
            } else {
                getLogger().warning("Не удалось зарегистрировать плейсхолдер %bounty%!");
            }
        } else {
            getLogger().warning("PlaceholderAPI не найден! Плейсхолдеры (%bounty%) не будут работать.");
        }
    }

    @Override
    public void onDisable() {
        databaseManager.close();
    }

    public BountyManager getBountyManager() {
        return bountyManager;
    }

    public boolean isPlaceholderAPIEnabled() {
        return getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;
    }
}