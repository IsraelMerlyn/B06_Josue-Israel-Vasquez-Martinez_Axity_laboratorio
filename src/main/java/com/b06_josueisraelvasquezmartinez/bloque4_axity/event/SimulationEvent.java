package com.b06_josueisraelvasquezmartinez.bloque4_axity.event;

import java.util.Random;

import com.b06_josueisraelvasquezmartinez.bloque4_axity.persistence.EventRecord;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.simulation.ParkState;

public interface SimulationEvent {
    String getName();

    String getDescription();

    double getProbability();

    // Contrato de ejecución táctica sobre los componentes del parque
    void execute(ParkState state, Random rng);

    // Mapea la contingencia a un Record inmutable listo para guardarse en la DB
    EventRecord toRecord(long step, String affectedEntities);
}
