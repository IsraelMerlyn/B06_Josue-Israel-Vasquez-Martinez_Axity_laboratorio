package com.b06_josueisraelvasquezmartinez.bloque4_axity.zone;

import java.util.Random;

import com.b06_josueisraelvasquezmartinez.bloque4_axity.config.ParkConfig;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.Tourist;

public class CentralHub implements ParkZone {
    private final String name;
    private final double souvenirPrice;
    private final double purchaseProbability;
    private int currentOccupancy;

    public CentralHub() {
        ParkConfig config = ParkConfig.getInstance();
        this.name = "Recinto Central";
        this.souvenirPrice = config.getDouble("hub.souvenirPrice", 15.0);
        this.purchaseProbability = config.getDouble("hub.souvenirPurchaseProbability", 0.4);
        this.currentOccupancy = 0;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean hasCapacity() {
        return true;
    }

    @Override
    public int getCurrentOccupancy() {
        return this.currentOccupancy;
    }

    @Override
    public int getMaxCapacity() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void enter(Tourist tourist) {
        if (tourist != null) {
            this.currentOccupancy++;
            tourist.recordVisit(this.name);
        }
    }

    @Override
    public void exit(Tourist tourist) {
        if (tourist != null && this.currentOccupancy > 0) {
            this.currentOccupancy--;
        }
    }

    /**
     * Simula la estancia del turista en la plaza central y su interacción con la
     * tienda
     * 
     * @param tourist El visitante actual
     * @param rng     El generador de números aleatorios unificado del parque
     */
    public void visit(Tourist tourist, Random rng) {
        if (tourist == null || rng == null)
            return;

        // Evaluación de probabilidad: Si cae dentro del rango, se efectúa la venta
        if (rng.nextDouble() < this.purchaseProbability) {
            tourist.spend(this.souvenirPrice);
            System.out.println(
                    "🛍️ El Turista [" + tourist.getName() + "] compró un souvenir por $" + this.souvenirPrice);
        }
    }
}
