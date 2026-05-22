package com.b06_josueisraelvasquezmartinez.bloque4_axity.model;

import lombok.Getter;

@Getter
public abstract class Dinosaur {
    private final int id;
    private final String name;
    private final String species;
    private final double feedingCostPerDay;
    private DinosaurStatus status;

    public Dinosaur(int id, String name, String species, double feedingCostPerDay) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.feedingCostPerDay = feedingCostPerDay;
        this.status = DinosaurStatus.IN_ENCLOSURE; // Todos inician seguros
    }

    // Contratos abstractos que cada tipo de dinosaurio debe responder
    public abstract String getDiet();

    public abstract double getDangerLevel();

    // Métodos de negocio para alterar el estado del espécimen
    public void escape() {
        this.status = DinosaurStatus.ESCAPED;
    }

    public void recapture() {
        this.status = DinosaurStatus.RECAPTURED;
    }

    public void returnToEnclosure() {
        this.status = DinosaurStatus.IN_ENCLOSURE;
    }
}
