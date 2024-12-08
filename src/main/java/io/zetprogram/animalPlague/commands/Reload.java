package io.zetprogram.animalPlague.commands;

import io.zetprogram.animalPlague.AnimalPlague;
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
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            _plugin.reloadConfig();
            ((AnimalPlague)_plugin).loadConfigValues();
            commandSender.sendMessage("§aConfig erfolgreich neu geladen!");
            return true;
        }
        return false;
    }
}
