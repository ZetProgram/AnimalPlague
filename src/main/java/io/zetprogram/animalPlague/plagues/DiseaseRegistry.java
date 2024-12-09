package io.zetprogram.animalPlague.plagues;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiseaseRegistry {
    public static final Map<String, List<String>> diseaseEntities = new HashMap<>();

    static {
        diseaseEntities.put("OverpopulationSyndrome", new ArrayList<>());
    }

    public static void registerEntityForDisease(String disease, String entityName) {
        diseaseEntities.computeIfAbsent(disease, k -> new ArrayList<>()).add(entityName);
    }

    public static List<String> getEntitiesForDisease(String disease) {
        return diseaseEntities.getOrDefault(disease, new ArrayList<>());
    }
}
