package com.b06_josueisraelvasquezmartinez.bloque4_axity.model;

import java.time.LocalDateTime;

public record Ticket(
        long id,
        int touristId,
        double price,
        String category,
        LocalDateTime issuedAt) {
}