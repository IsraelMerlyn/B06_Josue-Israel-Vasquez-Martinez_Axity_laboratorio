package com.b06_josueisraelvasquezmartinez.bloque4_axity.persistence;

import java.time.LocalDateTime;

public record RevenueRecord(
        long id,
        String type, // Ej. BOLETO, SOUVENIR, SPA, ENCIERRO
        double amount,
        int touristId,
        String zone,
        LocalDateTime timestamp) {
}