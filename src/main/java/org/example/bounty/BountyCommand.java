package org.example.bounty;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BountyCommand implements CommandExecutor {
    private final BountyManager bountyManager;
    private final BountyPlugin plugin;

    public BountyCommand(BountyManager bountyManager) {
        this.bountyManager = bountyManager;
        this.plugin = (BountyPlugin) JavaPlugin.getProvidingPlugin(BountyPlugin.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Эта команда только для игроков!");
                return true;
            }
            Player player = (Player) sender;
            String message = "&aВаш баунти: %bounty%";
            if (plugin.isPlaceholderAPIEnabled()) {
                message = PlaceholderAPI.setPlaceholders(player, message);
            } else {
                message = message.replace("%bounty%", String.format("%.2f", bountyManager.getBounty(player.getUniqueId())));
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            return true;
        }

        if (!sender.hasPermission("bounty.admin")) {
            sender.sendMessage(ChatColor.RED + "Нет прав!");
            return true;
        }

        if (args.length == 1) {
            Player target = sender.getServer().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Игрок " + args[0] + " не найден");
                return true;
            }
            String message = "&aБаунти игрока " + target.getName() + ": %bounty%";
            if (plugin.isPlaceholderAPIEnabled()) {
                message = PlaceholderAPI.setPlaceholders(target, message);
            } else {
                message = message.replace("%bounty%", String.format("%.2f", bountyManager.getBounty(target.getUniqueId())));
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Использование: /bounty [set|add|remove] <player> <amount>");
            return false;
        }

        String action = args[0].toLowerCase();
        Player target = sender.getServer().getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Игрок " + args[1] + " не найден");
            return false;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Неверное значение: " + args[2]);
            return false;
        }

        switch (action) {
            case "set":
                bountyManager.setBounty(target.getUniqueId(), amount);
                sender.sendMessage(ChatColor.GREEN + "Баунти для " + target.getName() + " установлено: " + amount);
                break;
            case "add":
                bountyManager.addBounty(target.getUniqueId(), amount);
                sender.sendMessage(ChatColor.GREEN + "Добавлено " + amount + " к баунти " + target.getName());
                break;
            case "remove":
                bountyManager.removeBounty(target.getUniqueId(), amount);
                sender.sendMessage(ChatColor.GREEN + "Убрано " + amount + " из баунти " + target.getName());
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Неизвестная команда: " + args[0]);
                return false;
        }
        return true;
    }
}