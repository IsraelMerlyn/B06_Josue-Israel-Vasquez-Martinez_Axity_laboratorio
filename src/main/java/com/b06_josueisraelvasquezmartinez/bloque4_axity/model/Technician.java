package com.b06_josueisraelvasquezmartinez.bloque4_axity.model;

import com.b06_josueisraelvasquezmartinez.bloque4_axity.zone.PowerPlant;

import java.util.Optional;
import java.util.List;

public class Technician extends Worker {
    public Technician(int id, String name, double dailySalary) {
        super(id, name, dailySalary);
    }

    @Override
    public String getRole() {
        return "TECHNICIAN";
    }

    public void repairIfNeeded(PowerPlant plant, List<Vehicle> vehicles) {
        if (plant == null || vehicles == null)
            return;

        // Si la planta funciona perfectamente, el técnico no interviene
        if (plant.isOperational()) {
            return;
        }

        System.out
                .println("🔧 El Técnico [" + getName() + "] detectó la avería y busca transporte para ir al sector...");

        // Busca el primer transporte en estado AVAILABLE usando Streams de Java
        Optional<Vehicle> availableVehicle = vehicles.stream()
                .filter(v -> v.getStatus() == VehicleStatus.AVAILABLE)
                .findFirst();

        if (availableVehicle.isPresent()) {
            Vehicle vehicle = availableVehicle.get();

            vehicle.use(); // Cambia el vehículo a IN_USE
            System.out.println(" Vehículo [" + vehicle.getId() + "] tomado por el técnico para la reparación.");

            plant.repair(); // Ejecuta la reparación de la planta

            vehicle.free(); // Devuelve el vehículo a AVAILABLE
            System.out.println(" Vehículo [" + vehicle.getId() + "] devuelto al taller central.");
        } else {
            System.err.println("¡Imposible reparar! No hay vehículos de mantenimiento disponibles en este paso.");
        }
    }
}
