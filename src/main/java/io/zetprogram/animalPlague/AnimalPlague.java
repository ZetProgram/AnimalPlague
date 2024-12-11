package io.zetprogram.animalPlague;

import io.zetprogram.animalPlague.commands.AnimalPlagueCommand;
import io.zetprogram.animalPlague.plagues.DiseaseRegistry;
import io.zetprogram.animalPlague.plagues.Population;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class AnimalPlague extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfigValues();
        Bukkit.getPluginManager().registerEvents(this, this);

        registerCommands();

        registerPlagues();

        this.getLogger().info("Plugin Startet");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    private void registerPlagues() {
        new Population(this);
    }

    private void registerCommands() {
        getCommand("animalplague").setExecutor(new AnimalPlagueCommand(this));
    }


    public void loadConfigValues() {
        FileConfiguration config = getConfig();

        registerEntitiesFromConfig(config);
    }

    private void registerEntitiesFromConfig(FileConfiguration config) {
        DiseaseRegistry.diseaseEntities.clear();

        ConfigurationSection plaguesSection = config.getConfigurationSection("Plagues");
        if (plaguesSection == null) {
            this.getLogger().warning("No plagues found in the configuration!");
            return;
        }

        for (String plagueName : plaguesSection.getKeys(false)) {
            ConfigurationSection plagueSection = plaguesSection.getConfigurationSection(plagueName);

            if (plagueSection != null) {
                boolean enabled = plagueSection.getBoolean("enabled", false);
                if (!enabled) {
                    this.getLogger().info("Skipping disabled plague: " + plagueName);
                    continue;
                }

                List<String> entities = plagueSection.getStringList("entites");
                if (entities.isEmpty()) {
                    this.getLogger().warning("No entities found for plague: " + plagueName);
                    continue;
                }

                DiseaseRegistry.diseaseEntities.put(plagueName, new ArrayList<>(entities));
                this.getLogger().info("Registered plague '" + plagueName + "' with entities: " + entities);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        ItemStack infoStick = new ItemStack(Material.STICK);
        ItemMeta infoStickMeta = infoStick.getItemMeta();
        infoStickMeta.setDisplayName(ChatColor.GREEN + "Infostick");
        infoStick.setItemMeta(infoStickMeta);

        if (Objects.equals(event.getPlayer().getItemOnCursor().getItemMeta().displayName(), infoStick.getItemMeta().displayName())) {
            event.getPlayer().sendMessage("EntityType: " + event.getRightClicked().getType().name());
        }
    }
}
