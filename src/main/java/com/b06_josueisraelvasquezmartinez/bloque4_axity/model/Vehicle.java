package com.b06_josueisraelvasquezmartinez.bloque4_axity.model;

import lombok.Getter;

@Getter
public class Vehicle {
    private final int id;
    private VehicleStatus status;
    private int repairCountdown;

    public Vehicle(int id) {
        this.id = id;
        this.status = VehicleStatus.AVAILABLE;
        this.repairCountdown = 0;
    }

    public void use() {
        if (this.status == VehicleStatus.AVAILABLE) {
            this.status = VehicleStatus.IN_USE;
        }
    }

    public void free() {
        if (this.status == VehicleStatus.IN_USE) {
            this.status = VehicleStatus.AVAILABLE;
        }
    }

    public void markBroken(int stepsToRepair) {
        this.status = VehicleStatus.BROKEN;
        this.repairCountdown = stepsToRepair;
    }

    // Simulación temporal: reduce el tiempo de reparación paso a paso
    public void tick() {
        if (this.status == VehicleStatus.BROKEN) {
            this.repairCountdown--;
            if (this.repairCountdown <= 0) {
                this.status = VehicleStatus.AVAILABLE;
                this.repairCountdown = 0;
                System.out.println(" Vehículo Mantenimiento [" + id + "] reparado automáticamente y disponible.");
            }
        }
    }
}
