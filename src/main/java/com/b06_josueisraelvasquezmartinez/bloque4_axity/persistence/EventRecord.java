package com.b06_josueisraelvasquezmartinez.bloque4_axity.persistence;

import java.time.LocalDateTime;

public record EventRecord(
        long id,
        long step,
        String eventName, // Ej. APAGON_MASIVO, ESCAPE_DINOSAURIO
        String description,
        String affectedEntities,
        LocalDateTime timestamp) {
}