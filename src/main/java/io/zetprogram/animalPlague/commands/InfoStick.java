package io.zetprogram.animalPlague.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class InfoStick implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.GRAY + "Only players can use this command!");
            return true;
        }

        ItemStack infoStick = new ItemStack(Material.STICK);
        ItemMeta infoStickMeta = infoStick.getItemMeta();
        infoStickMeta.setDisplayName(ChatColor.GREEN + "Infostick");
        infoStick.setItemMeta(infoStickMeta);

        player.getInventory().addItem(infoStick);
        player.sendMessage(ChatColor.GREEN + "You received an InfoStick!");
        return true;
    }
}
