package io.zetprogram.animalPlague.plagues;

import java.util.UUID;

public class InfectionState {
    UUID entityId;
    long infectionTimestamp; // Zeit der Infektion
    long effectStartTimestamp; // Zeit, wann der Effekt beginnt
    long deathTimestamp; // Zeit, wann der Tod eintreten soll

    public InfectionState(UUID entityId, long infectionTimestamp, long effectStartTimestamp, long deathTimestamp) {
        this.entityId = entityId;
        this.infectionTimestamp = infectionTimestamp;
        this.effectStartTimestamp = effectStartTimestamp;
        this.deathTimestamp = deathTimestamp;
    }
}
