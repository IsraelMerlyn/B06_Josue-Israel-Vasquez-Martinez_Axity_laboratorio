package com.b06_josueisraelvasquezmartinez.bloque4_axity.event;

import java.time.LocalDateTime;
import java.util.Random;

import com.b06_josueisraelvasquezmartinez.bloque4_axity.config.ParkConfig;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.TouristStatus;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.persistence.EventRecord;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.simulation.ParkState;

public class StormEvent implements SimulationEvent {
    private final String name;
    private final String description;
    private final double probability;

    public StormEvent() {
        ParkConfig config = ParkConfig.getInstance();
        this.name = "TORMENTA_TORRENCIAL";
        this.description = "Tormenta tropical con actividad eléctrica severa sobre la región.";
        this.probability = config.getDouble("event.storm.probability", 0.04);
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
        if (state == null) return;

        // Buscar turistas activos y forzar el registro de evacuación preventiva
        long count = state.getAllTourists().stream()
                .filter(t -> t.getStatus() == TouristStatus.IN_PARK)
                .peek(t -> t.recordVisit("Evacuación"))
                .count();

        // Cargo operativo por activación de refugios temporales
        double stormCost = 500.0;
        state.addExpense(stormCost);

        state.registerActiveEvent(this.name);
        System.out.println("⛈️ [EVENTO] Tormenta Torrencial activa. Refugiando a " + count + " visitantes de forma segura.");
    }

    @Override
    public EventRecord toRecord(long step, String affectedEntities) {
        return new EventRecord(0, step, this.name, this.description, affectedEntities, LocalDateTime.now());
    }
}
