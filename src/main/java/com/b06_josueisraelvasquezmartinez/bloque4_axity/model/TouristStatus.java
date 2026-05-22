package com.b06_josueisraelvasquezmartinez.bloque4_axity.model;

public enum TouristStatus {
    WAITING, // En la fila de abordaje/arribo, fuera del parque
    IN_PARK, // Dentro del parque interactuando con las zonas
    ATTACKED, // Afectado por un evento crítico de dinosaurio
    EXITED // Salió del parque de forma segura
}
