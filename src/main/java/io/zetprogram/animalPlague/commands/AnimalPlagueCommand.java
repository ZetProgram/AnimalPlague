package io.zetprogram.animalPlague.commands;

import io.zetprogram.animalPlague.AnimalPlague;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AnimalPlagueCommand implements CommandExecutor {

    private final AnimalPlague plugin;
    private final List<String> registeredEntityNames = new ArrayList<String>();

    public AnimalPlagueCommand(AnimalPlague plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§aUsage: /animalplague <reload|infostick>");
            return false;
        }

        // Delegate Subcommands
        if (args[0].equalsIgnoreCase("reload")) {
            new Reload(plugin).onCommand(sender, command, label, args);
        } else if (args[0].equalsIgnoreCase("infostick")) {
            new InfoStick().onCommand(sender, command, label, args);
        } else {
            sender.sendMessage("§cUnknown subcommand: " + args[0]);
            return false;
        }

        return true;
    }
}
