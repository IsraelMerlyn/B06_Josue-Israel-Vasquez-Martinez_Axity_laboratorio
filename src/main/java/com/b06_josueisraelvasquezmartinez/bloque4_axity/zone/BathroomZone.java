package com.b06_josueisraelvasquezmartinez.bloque4_axity.zone;

import java.util.HashMap;
import java.util.Random;
import java.util.Iterator;
import java.util.Map;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.config.ParkConfig;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.Tourist;

public class BathroomZone implements ParkZone {
    private final String name;
    private final int maxCapacity;
    private final int useDurationSteps;
    private final double spaPrice;
    private final double spaPurchaseProbability;

    // Almacena los turistas adentro y mapea cuántos pasos les quedan antes de salir
    private final Map<Tourist, Integer> occupants;

    public BathroomZone() {
        ParkConfig config = ParkConfig.getInstance();
        this.name = "Baños y Servicios";
        this.maxCapacity = config.getInt("bathroom.maxCapacity", 10);
        this.useDurationSteps = config.getInt("bathroom.useDurationSteps", 3);
        this.spaPrice = config.getDouble("bathroom.spaPrice", 20.0);
        this.spaPurchaseProbability = config.getDouble("bathroom.spaPurchaseProbability", 0.2);
        this.occupants = new HashMap<>();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean hasCapacity() {
        return this.occupants.size() < this.maxCapacity;
    }

    @Override
    public int getCurrentOccupancy() {
        return this.occupants.size();
    }

    @Override
    public int getMaxCapacity() {
        return this.maxCapacity;
    }

    @Override
    public void enter(Tourist tourist) {
        if (tourist != null && hasCapacity()) {
            this.occupants.put(tourist, this.useDurationSteps);
            tourist.recordVisit(this.name);
        }
    }

    @Override
    public void exit(Tourist tourist) {
        if (tourist != null) {
            this.occupants.remove(tourist);
        }
    }

    /**
     * Intenta registrar el ingreso controlado de un turista a los servicios
     * 
     * @param tourist El visitante que requiere usar la zona
     * @param rng     El generador de números aleatorios para la compra cruzada de
     *                SPA
     * @return true si logró entrar, false si estaba lleno o ya se encontraba
     *         adentro
     */
    public boolean tryEnter(Tourist tourist, Random rng) {
        if (tourist == null || rng == null || !hasCapacity() || occupants.containsKey(tourist)) {
            return false;
        }

        // Registrar entrada fijando su tiempo de permanencia completo
        this.occupants.put(tourist, this.useDurationSteps);
        tourist.recordVisit(this.name);
        System.out.println("Categoría: 🚻 El Turista [" + tourist.getName() + "] ingresó a la zona de Baños.");

        // Simulación de venta de servicios adicionales (SPA)
        if (rng.nextDouble() < this.spaPurchaseProbability) {
            tourist.spend(this.spaPrice);
            System.out.println(
                    " ¡Tratamiento SPA Premium adquirido! [" + tourist.getName() + "] gastó $" + this.spaPrice);
        }

        return true;
    }

    /**
     * Avanza el tiempo interno de la zona, disminuyendo la permanencia de los
     * ocupantes
     */
    public void tick() {
        Iterator<Map.Entry<Tourist, Integer>> iterator = occupants.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Tourist, Integer> entry = iterator.next();
            int remainingSteps = entry.getValue() - 1;

            if (remainingSteps <= 0) {
                System.out.println("🧻 El Turista [" + entry.getKey().getName()
                        + "] completó su tiempo en los Baños y se retira.");
                iterator.remove(); // Eliminación segura del mapa en tiempo de ejecución
            } else {
                entry.setValue(remainingSteps); // Actualiza los pasos restantes
            }
        }
    }
}
