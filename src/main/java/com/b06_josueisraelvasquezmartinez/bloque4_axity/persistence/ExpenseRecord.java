package com.b06_josueisraelvasquezmartinez.bloque4_axity.persistence;

import java.time.LocalDateTime;

public record ExpenseRecord(
        long id,
        String type, // Ej. SALARIOS, ENERGIA, MANTENIMIENTO, REPARACION
        double amount,
        String description,
        LocalDateTime timestamp) {
}
