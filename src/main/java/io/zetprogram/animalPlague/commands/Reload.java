package io.zetprogram.animalPlague.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class Reload implements CommandExecutor {

    private final JavaPlugin _plugin;

    public Reload(JavaPlugin plugin) {
        this._plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("animalplague.reload")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        _plugin.reloadConfig();
        sender.sendMessage("§aConfiguration reloaded.");
        return true;
    }
}
