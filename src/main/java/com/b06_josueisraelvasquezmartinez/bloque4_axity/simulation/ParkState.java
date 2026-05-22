package com.b06_josueisraelvasquezmartinez.bloque4_axity.simulation;

import java.util.ArrayList;
import java.util.Random;

import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.Dinosaur;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.DinosaurStatus;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.Tourist;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.TouristStatus;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.Vehicle;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.VehicleStatus;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.Worker;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.zone.PowerPlant;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ParkState {
    private int currentStep;
    private double totalRevenue;
    private double totalExpenses;

    private final List<Tourist> allTourists;
    private final List<Dinosaur> allDinosaurs;
    private final List<Worker> allWorkers;
    private final List<Vehicle> vehicles;

    // Controladores de estado
    private final List<String> activeEventNames;
    private final Random rng;

    @Setter
    private boolean dealsHourActive;
    @Setter
    private double currentDiscount;

    @Getter
    @Setter
    private PowerPlant powerPlant;

    public ParkState(Random rng) {
        this.currentStep = 0;
        this.totalRevenue = 0.0;
        this.totalExpenses = 0.0;
        this.allTourists = new ArrayList<>();
        this.allDinosaurs = new ArrayList<>();
        this.allWorkers = new ArrayList<>();
        this.vehicles = new ArrayList<>();
        this.activeEventNames = new ArrayList<>();
        this.rng = rng;
        this.dealsHourActive = false;
        this.currentDiscount = 0.0;
    }

    public void incrementStep() {
        this.currentStep++;
    }

    public void addRevenue(double amount) {
        if (amount > 0)
            this.totalRevenue += amount;
    }

    public void addExpense(double amount) {
        if (amount > 0)
            this.totalExpenses += amount;
    }

    // Limpia la bitácora de alertas
    public void clearActiveEventsForStep() {
        this.activeEventNames.clear();
        this.dealsHourActive = false;
        this.currentDiscount = 0.0;
    }

    public void registerActiveEvent(String eventName) {
        if (eventName != null)
            this.activeEventNames.add(eventName);
    }

    // Métricas calculadas para el sistema de monitoreo en tiempo real
    public long countActiveTourists() {
        return allTourists.stream()
                .filter(t -> t.getStatus() == TouristStatus.IN_PARK)
                .count();
    }

    public long countDinosaursInEnclosure() {
        return allDinosaurs.stream()
                .filter(d -> d.getStatus() == DinosaurStatus.IN_ENCLOSURE)
                .count();
    }

    public long countUnavailableVehicles() {
        return vehicles.stream()
                .filter(v -> v.getStatus() == VehicleStatus.BROKEN || v.getStatus() == VehicleStatus.IN_USE)
                .count();
    }

    public int getCurrentStep() {
        return this.currentStep;
    }

    public double getTotalRevenue() {
        return this.totalRevenue;
    }

    public double getTotalExpenses() {
        return this.totalExpenses;
    }

    public List<Tourist> getAllTourists() {
        return this.allTourists;
    }

    public List<Dinosaur> getAllDinosaurs() {
        return this.allDinosaurs;
    }

    public List<Worker> getAllWorkers() {
        return this.allWorkers;
    }

    public List<Vehicle> getVehicles() {
        return this.vehicles;
    }

    public List<String> getActiveEventNames() {
        return this.activeEventNames;
    }

    public Random getRng() {
        return this.rng;
    }

    public boolean isDealsHourActive() {
        return this.dealsHourActive;
    }

    public void setDealsHourActive(boolean dealsHourActive) {
        this.dealsHourActive = dealsHourActive;
    }

    public double getCurrentDiscount() {
        return this.currentDiscount;
    }

    public void setCurrentDiscount(double currentDiscount) {
        this.currentDiscount = currentDiscount;
    }

    public PowerPlant getPowerPlant() {
        return this.powerPlant;
    }

    public void setPowerPlant(PowerPlant powerPlant) {
        this.powerPlant = powerPlant;
    }
}
