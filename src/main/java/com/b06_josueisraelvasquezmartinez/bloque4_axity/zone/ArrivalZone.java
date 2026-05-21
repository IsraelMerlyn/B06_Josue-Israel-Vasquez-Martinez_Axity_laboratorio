package com.b06_josueisraelvasquezmartinez.bloque4_axity.zone;

import java.util.LinkedList;
import java.util.Queue;

import com.b06_josueisraelvasquezmartinez.bloque4_axity.config.ParkConfig;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.Tourist;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.TouristStatus;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ArrivalZone implements ParkZone {
    private final String name;
    private final int maxCapacity;
    private final double ticketPrice;

    private final Queue<Tourist> waitingQueue;
    private int currentOccupancy;

    public ArrivalZone() {
        ParkConfig config = ParkConfig.getInstance();
        this.name = "Lugar de Arribo";
        this.maxCapacity = config.getInt("arrival.maxCapacity", 30);
        this.ticketPrice = config.getDouble("arrival.ticketPrice", 25.0);
        this.waitingQueue = new LinkedList<>();
        this.currentOccupancy = 0;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean hasCapacity() {
        return this.currentOccupancy < this.maxCapacity;
    }

    @Override
    public int getCurrentOccupancy() {
        return this.currentOccupancy;
    }

    @Override
    public int getMaxCapacity() {
        return this.maxCapacity;
    }

    // Un turista llega de fuera y se forma en la fila de espera externa
    @Override
    public void enter(Tourist tourist) {
        if (tourist != null) {
            waitingQueue.add(tourist);
        }
    }

    // Libera espacio en la zona cuando el turista se mueve hacia el interior del
    @Override
    public void exit(Tourist tourist) {
        if (tourist != null && this.currentOccupancy > 0) {
            this.currentOccupancy--;
        }
    }

    /**
     * Procesa un lote de turistas formados, les vende su boleto y les da entrada al
     * parque
     * 
     * @param batchSize Cantidad máxima de turistas a procesar en este paso
     * @return Lista de turistas que lograron ingresar con éxito en este lote
     */
    public List<Tourist> processBatch(int batchSize) {
        List<Tourist> acceptedTourists = new LinkedList<>();
        int processed = 0;

        while (processed < batchSize && !waitingQueue.isEmpty() && hasCapacity()) {
            Tourist tourist = waitingQueue.poll();

            if (tourist != null) {
                // Lógica de negocio y recaudación
                tourist.spend(this.ticketPrice);
                tourist.changeStatus(TouristStatus.IN_PARK);
                tourist.recordVisit(this.name);

                this.currentOccupancy++;
                acceptedTourists.add(tourist);

                System.out.println("🎟️ Boleto vendido a [" + tourist.getName() + "]. Ingresa al parque.");
            }
            processed++;
        }
        return acceptedTourists;
    }

    // Método de utilidad para auditar cuántos turistas siguen esperando en la fila
    public int getWaitingQueueSize() {
        return this.waitingQueue.size();
    }
}
