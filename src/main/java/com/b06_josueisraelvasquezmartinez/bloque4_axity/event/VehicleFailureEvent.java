package com.b06_josueisraelvasquezmartinez.bloque4_axity.event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import com.b06_josueisraelvasquezmartinez.bloque4_axity.config.ParkConfig;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.Vehicle;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.VehicleStatus;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.persistence.EventRecord;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.simulation.ParkState;

public class VehicleFailureEvent implements SimulationEvent {
    private final String name;
    private final String description;
    private final double probability;

    public VehicleFailureEvent() {
        ParkConfig config = ParkConfig.getInstance();
        this.name = "AVERIA_VEHICULO";
        this.description = "Falla mecánica fortuita en una unidad de transporte. Requiere reparación en taller.";
        this.probability = config.getDouble("event.vehicle.probability", 0.06);
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
        if (state == null || state.getVehicles().isEmpty())
            return;

        // Filtrar vehículos que estén listos para operar
        List<Vehicle> operationalVehicles = state.getVehicles().stream()
                .filter(v -> v.getStatus() == VehicleStatus.AVAILABLE)
                .toList();

        if (operationalVehicles.isEmpty()) {
            return; // Si todos están ocupados o rotos, no podemos descomponer otro
        }

        // Seleccionar uno al azar y averiarlo por 3 pasos de simulación
        Vehicle brokenVehicle = operationalVehicles.get(rng.nextInt(operationalVehicles.size()));
        brokenVehicle.markBroken(3);

        String affected = "Vehículo ID: " + brokenVehicle.getId();
        state.registerActiveEvent(this.name);
        System.err.println(
                "🔧 [LOG] El Vehículo de Mantenimiento [" + brokenVehicle.getId() + "] sufrió una avería mecánica.");
    }

    @Override
    public EventRecord toRecord(long step, String affectedEntities) {
        return new EventRecord(0, step, this.name, this.description, affectedEntities, LocalDateTime.now());
    }
}
