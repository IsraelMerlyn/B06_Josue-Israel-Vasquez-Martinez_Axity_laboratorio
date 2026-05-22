package com.b06_josueisraelvasquezmartinez.bloque4_axity.zone;

import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.Tourist;

public interface ParkZone {
    // Devuelve el nombre único identificador de la zona
    String getName();

    // Verifica si la zona tiene espacio disponible para recibir más turistas
    boolean hasCapacity();

    // Devuelve la cantidad actual de visitantes dentro de la zona
    int getCurrentOccupancy();

    // Devuelve el límite máximo de personas permitidas en la zona
    int getMaxCapacity();

    // Registra la entrada de un turista a la zona, aplicando la lógica interna de
    // esa área
    void enter(Tourist tourist);

    // Controla la salida de un turista de la zona, liberando su espacio asignado
    void exit(Tourist tourist);
}
