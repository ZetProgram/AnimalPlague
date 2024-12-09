package io.zetprogram.animalPlague.plagues;

import io.zetprogram.animalPlague.AnimalPlague;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Population implements Listener {

    private final AnimalPlague _plugin;

    private static int check_radius = 10;
    private static int max_animals = 5;
    private static double spread_chance = 0.3;


    private final Set<UUID> infectedAnimals = new HashSet<UUID>();

    public Population(AnimalPlague plugin) {
        this._plugin = plugin;

        String plagueName = "OverpopulationSyndrome";
        check_radius = plugin.getConfig().getInt("Plagues." + plagueName + ".infection.check-radius", 10);
        max_animals = plugin.getConfig().getInt("Plagues." + plagueName + ".infection.max-animals", 5);
        long infection_time = plugin.getConfig().getLong("Plagues." + plagueName + ".infection.infection-time", 300L);
        spread_chance = plugin.getConfig().getDouble("Plagues." + plagueName + ".infection.spread-chance", 0.3);

        Bukkit.getPluginManager().registerEvents(this, plugin);
        new BukkitRunnable() {
            @Override
            public void run() {
                spreadInfection();
                checkAndInfectAnimals(DiseaseRegistry.diseaseEntities.get("OverpopulationSyndrome"));
            }
        }.runTaskTimer(plugin, 0, infection_time);

    }

    private void checkAndInfectAnimals(List<String> registeredEntities) {
        for (Entity entity : Bukkit.getWorlds().get(0).getEntities()) {
            if (infectedAnimals.contains(entity.getUniqueId())) continue;

            if (registeredEntities.contains(entity.getType().name())) {
                Location location = entity.getLocation();
                List<Entity> nearbyEntities = (List<Entity>) location.getWorld().getNearbyEntities(location, check_radius, check_radius, check_radius);

                List<Entity> candidateEntities = new ArrayList<>();
                for (Entity nearby : nearbyEntities) {
                    if (!infectedAnimals.contains(nearby.getUniqueId()) && registeredEntities.contains(nearby.getType().name())) {
                        boolean isAlreadyInfectedNearby = nearbyEntities.stream().anyMatch(e -> infectedAnimals.contains(e.getUniqueId()));

                        if (isAlreadyInfectedNearby) {
                            continue;
                        }

                        candidateEntities.add(nearby);
                    }
                }

                if (candidateEntities.size() > max_animals) {
                    Entity toInfect = candidateEntities.get(new Random().nextInt(candidateEntities.size()));

                    infectedAnimals.add(toInfect.getUniqueId());

                    if (toInfect instanceof LivingEntity livingEntity) {
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, Integer.MAX_VALUE, 0, true, true));
                    }

                    _plugin.getLogger().info("Infected " + toInfect.getType().name() + " with disease: " + "OverpopulationSyndrome");
                    break;
                }
            }
        }
    }

    private void spreadInfection() {
        List<UUID> newInfections = new ArrayList<>();

        for (UUID infectedId : infectedAnimals) {
            Entity infectedEntity = Bukkit.getEntity(infectedId);
            if (infectedEntity == null || !(infectedEntity instanceof LivingEntity)) {
                continue;
            }

            LivingEntity livingInfected = (LivingEntity) infectedEntity;
            Location infectedLocation = livingInfected.getLocation();
            List<Entity> nearbyEntities = (List<Entity>) infectedLocation.getWorld().getNearbyEntities(infectedLocation, check_radius, check_radius, check_radius);

            for (Entity nearby : nearbyEntities) {
                if (nearby instanceof LivingEntity && !infectedAnimals.contains(nearby.getUniqueId()) && !infectedEntity.getUniqueId().equals(nearby.getUniqueId()) // Verhindere, dass sich das Tier selbst infiziert
                        && registeredEntityTypesMatch(infectedEntity, nearby) // Überprüfe, ob der Typ übereinstimmt
                        && spreadChance()) { // Überprüfe die Chance, dass die Krankheit sich ausbreitet

                    newInfections.add(nearby.getUniqueId());
                    break; // Breche die Schleife nach der ersten Infektion ab
                }
            }
        }

        if (!newInfections.isEmpty()) {
            UUID newInfectedId = newInfections.get(0); // Wähle das erste neue infizierte Tier
            infectedAnimals.add(newInfectedId);

            Entity newInfectedEntity = Bukkit.getEntity(newInfectedId);
            if (newInfectedEntity instanceof LivingEntity newInfected) {
                newInfected.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, Integer.MAX_VALUE, 0, true, true));
            }

            _plugin.getLogger().info("Infected " + newInfectedEntity.getType().name() + " with disease: " + "OverpopulationSyndrome");
        }
    }

    private boolean registeredEntityTypesMatch(Entity infectedEntity, Entity nearbyEntity) {
        return infectedEntity.getType().equals(nearbyEntity.getType());
    }

    private boolean spreadChance() {
        return Math.random() < spread_chance;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAnimalDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();

        if (infectedAnimals.contains(entity.getUniqueId())) {
            infectedAnimals.remove(entity.getUniqueId());
        }
    }


}
