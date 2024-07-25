package com.lunarate.hiantplugin.commands;



public class UpgradeEnderChestCommand implements CommandExecutor {
    private Main plugin;

    public UpgradeEnderChestCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender.isOp())) {
            sender.sendMessage("You don't have permission to use this command.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("Usage: /upgradeenderchest <player>");
            return true;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage("Player not found.");
            return true;
        }

        EnderChestUpgrader.upgradeEnderChest(player);
        sender.sendMessage("Ender chest upgraded for " + player.getName());
        return true;
    }
}


