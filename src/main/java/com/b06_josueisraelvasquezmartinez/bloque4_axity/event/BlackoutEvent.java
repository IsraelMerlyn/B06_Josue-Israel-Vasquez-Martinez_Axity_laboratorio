package com.b06_josueisraelvasquezmartinez.bloque4_axity.event;

import java.time.LocalDateTime;
import java.util.Random;

import com.b06_josueisraelvasquezmartinez.bloque4_axity.config.ParkConfig;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.persistence.EventRecord;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.simulation.ParkState;

public class BlackoutEvent implements SimulationEvent {
    private final String name;
    private final String description;
    private final double probability;

    public BlackoutEvent() {
        ParkConfig config = ParkConfig.getInstance();
        this.name = "APAGON_MASIVO";
        this.description = "Sobrecarga en los transformadores centrales. Red eléctrica fuera de servicio.";
        this.probability = config.getDouble("event.blackout.probability", 0.03);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
    @Override
    public double getProbability() {
        return this.probability;
    }

    @Override
    public void execute(ParkState state, Random rng) {
        if (state == null || state.getPowerPlant() == null) return;

        // Forzar la caída de los sistemas de la planta
        state.getPowerPlant().triggerFailure();

        // El laboratorio exige añadir un costo operativo de castigo de $2000.0
        double penaltyCost = 2000.0;
        state.addExpense(penaltyCost);

        state.registerActiveEvent(this.name);
        System.err.println("🚨 [EVENTO] ¡Apagón Masivo detectado! Se aplicó un cargo de reparación de $" + penaltyCost);
    }

    @Override
    public EventRecord toRecord(long step, String affectedEntities) {
        return new EventRecord(0, step, this.name, this.description, affectedEntities, LocalDateTime.now());
    }
}
