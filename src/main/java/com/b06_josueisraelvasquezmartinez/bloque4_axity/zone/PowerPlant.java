package com.b06_josueisraelvasquezmartinez.bloque4_axity.zone;

import java.util.Random;

import com.b06_josueisraelvasquezmartinez.bloque4_axity.config.ParkConfig;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.Tourist;

public class PowerPlant implements ParkZone {
    private final String name;
    private final double consumptionPerStep;
    private final double failureProbability;

    private double currentEnergy;
    private boolean operational;

    public PowerPlant() {
        ParkConfig config = ParkConfig.getInstance();
        this.name = "Planta de Energía";
        this.currentEnergy = config.getDouble("powerplant.initialEnergy", 100.0);
        this.consumptionPerStep = config.getDouble("powerplant.consumptionPerStep", 1.5);
        this.failureProbability = config.getDouble("powerplant.failureProbability", 0.05);
        this.operational = true;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean hasCapacity() {
        return false; // Los turistas no tienen permitido el acceso aquí
    }

    @Override
    public int getCurrentOccupancy() {
        return 0;
    }

    @Override
    public int getMaxCapacity() {
        return 0; // Zona restringida de seguridad
    }

    @Override
    public void enter(Tourist tourist) {
        // No-op: Seguridad industrial impide el paso de civiles
    }

    @Override
    public void exit(Tourist tourist) {
        // No-op
    }

    public boolean isOperational() {
        return this.operational;
    }

    public double getCurrentEnergy() {
        return this.currentEnergy;
    }

    // Detona una falla forzada
    public void triggerFailure() {
        this.operational = false;
        this.currentEnergy = 0.0;
        System.err.println("¡Falla catastrófica en la Planta de Energía! Cortocircuito general.");
    }

    // Restaura los sistemas de la planta
    public void repair() {
        this.operational = true;
        this.currentEnergy = 100.0;
        System.out.println(" Planta de Energía reparada con éxito. Red eléctrica reestablecida.");
    }

    /**
     * Avanza el ciclo de la planta devorando combustible o sorteando fallas
     * fortuitas
     * 
     * @param rng Generador aleatorio unificado
     * @return El costo operativo generado en este step (Gasto)
     */
    public double tick(Random rng) {
        if (!this.operational) {
            return 0.0; // Si ya está apagada, no consume insumos operativos corrientes
        }

        // Consumo regular de energía por paso de tiempo
        this.currentEnergy = Math.max(0.0, this.currentEnergy - this.consumptionPerStep);

        // Si se nos acaba la energía por completo, la planta se apaga sola
        if (this.currentEnergy <= 0.0) {
            this.operational = false;
            System.err.println(" La Planta se quedó sin energía disponible. Entrando en modo Blackout.");
            return 0.0;
        }

        // Sorteo probabilístico de fallas fortuitas
        if (rng.nextDouble() < this.failureProbability) {
            triggerFailure();
        }

        return 0.0; // Retornamos cero, los costos exactos se registrarán en la persistencia del
                    // Engine
    }
}
