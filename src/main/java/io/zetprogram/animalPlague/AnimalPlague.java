package io.zetprogram.animalPlague;

import io.zetprogram.animalPlague.commands.Reload;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public final class AnimalPlague extends JavaPlugin implements Listener {

    private final List<String> registeredEntityNames = new ArrayList<String>();

    private static int CHECK_RADIUS = 10; // Default 10
    private static int MAX_ANIMALS = 5; // Default 5
    private static long INFECTION_TIME = 300L; // 15s (1s = 20L)

    private final Set<UUID> infectedAnimals = new HashSet<UUID>();

    @Override
    public void onEnable() {

        Bukkit.getPluginManager().registerEvents(this, this);

        getCommand("animalplague").setExecutor(new Reload(this));

        new BukkitRunnable() {
            @Override
            public void run() {
                checkAndInfectAnimals();
            }
        }.runTaskTimer(this, 0, INFECTION_TIME);

        this.getLogger().info("Plugin Startet");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void loadConfigValues() {
        FileConfiguration config = getConfig();

        CHECK_RADIUS = config.getInt("infection.check-radius");
        MAX_ANIMALS = config.getInt("infection.max-animals");
        INFECTION_TIME = config.getInt("infection.infection-time");

        registerEntitiesFromConfig(config);
    }

    private void registerEntitiesFromConfig(FileConfiguration config) {
        registeredEntityNames.addAll(config.getStringList("entites"));
    }

    private void checkAndInfectAnimals() {
        for (Entity entity : Bukkit.getWorlds().get(0).getEntities()) {
            if (registeredEntityNames.contains(entity.getType().name())) {
                Location location = entity.getLocation();
                List<Entity> nearbyEntities = (List<Entity>) location.getWorld().getNearbyEntities(location, CHECK_RADIUS, CHECK_RADIUS, CHECK_RADIUS);

                if (nearbyEntities.size() > MAX_ANIMALS) {
                    infectedAnimals.add(entity.getUniqueId());

                }
            }
        }
    }

    @EventHandler
    public void onAnimalDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();

        if (infectedAnimals.contains(entity.getUniqueId())) {
            event.getDrops().clear();
            event.getDrops().add(new ItemStack(Material.ROTTEN_FLESH, new Random().nextInt(3)));
            infectedAnimals.remove(entity.getUniqueId());
        }
    }
}
