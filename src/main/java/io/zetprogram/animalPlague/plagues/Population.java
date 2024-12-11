package io.zetprogram.animalPlague.plagues;

import io.zetprogram.animalPlague.AnimalPlague;
import org.bukkit.Bukkit;
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
        // Iterate over all living entities in the world
        for (Entity entity : Bukkit.getWorlds().get(0).getEntities()) {
            if (infectedAnimals.contains(entity.getUniqueId())) continue;

            if (registeredEntities.contains(entity.getType().name())) {
                List<LivingEntity> nearbyEntities = entity.getLocation().getNearbyEntities(check_radius, check_radius, check_radius).stream().filter(e -> e instanceof LivingEntity && !infectedAnimals.contains(e.getUniqueId())).map(e -> (LivingEntity) e).filter(e -> registeredEntities.contains(e.getType().name())).toList();

                if (nearbyEntities.size() > max_animals) {
                    LivingEntity toInfect = nearbyEntities.get(new Random().nextInt(nearbyEntities.size()));

                    infectEntity(toInfect, PotionEffectType.POISON);
                    break;
                }
            }
        }
    }

    private void spreadInfection() {
        List<Entity> newInfections = new ArrayList<>();

        for (UUID infectedId : infectedAnimals) {
            LivingEntity infectedEntity = (LivingEntity) Bukkit.getEntity(infectedId);
            if (infectedEntity == null) continue;

            List<LivingEntity> nearbyEntities = infectedEntity.getLocation().getNearbyEntities(check_radius, check_radius, check_radius).stream().filter(e -> e != null).map(e -> (LivingEntity) e).filter(e -> !infectedAnimals.contains(e.getUniqueId()) && e.getType().equals(infectedEntity.getType()) && Math.random() < spread_chance).toList();

            if (!nearbyEntities.isEmpty()) {
                newInfections.add(nearbyEntities.get(0)); // Only infect one per cycle
            }
        }

        newInfections.forEach(e -> infectEntity((LivingEntity) e, PotionEffectType.WITHER));
    }


    private void infectEntity(LivingEntity entity, PotionEffectType effectType) {
        infectedAnimals.add(entity.getUniqueId());
        entity.addPotionEffect(new PotionEffect(effectType, Integer.MAX_VALUE, 0, false, false));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAnimalDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();

        if (infectedAnimals.contains(entity.getUniqueId())) {
            infectedAnimals.remove(entity.getUniqueId());
        }
    }


}
