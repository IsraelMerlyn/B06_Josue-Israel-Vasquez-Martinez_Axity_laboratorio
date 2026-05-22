package com.b06_josueisraelvasquezmartinez.bloque4_axity.event;

import java.time.LocalDateTime;
import java.util.Random;

import com.b06_josueisraelvasquezmartinez.bloque4_axity.config.ParkConfig;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.persistence.EventRecord;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.simulation.ParkState;

public class DealsHourEvent implements SimulationEvent {
    private final String name;
    private final String description;
    private final double probability;

    public DealsHourEvent() {
        ParkConfig config = ParkConfig.getInstance();
        this.name = "HORA_DE_OFERTAS";
        this.description = "Promoción flash en la tienda central. 20% de descuento en todos los souvenirs.";
        this.probability = config.getDouble("event.deals.probability", 0.10); // Probabilidad más alta
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
        if (state == null)
            return;

        // Activar las rebajas en el estado global para este paso de tiempo
        state.setDealsHourActive(true);
        state.setCurrentDiscount(0.20); // 20% de descuento directo

        state.registerActiveEvent(this.name);
        System.out.println("🛍️ [PROMO] ¡Se activó la Hora de Ofertas! Descuentos del 20% habilitados en souvenirs.");
    }

    @Override
    public EventRecord toRecord(long step, String affectedEntities) {
        return new EventRecord(0, step, this.name, this.description, affectedEntities, LocalDateTime.now());
    }
}
