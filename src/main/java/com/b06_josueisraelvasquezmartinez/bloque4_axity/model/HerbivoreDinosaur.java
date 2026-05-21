package com.b06_josueisraelvasquezmartinez.bloque4_axity.model;

public class HerbivoreDinosaur extends Dinosaur {
    public HerbivoreDinosaur(int id, String name, String species) {
        // Los herbívoros consumen $200.0 diarios
        super(id, name, species, 200.0);
    }

    @Override
    public String getDiet() {
        return "HERBIVORE";
    }

    @Override
    public double getDangerLevel() {
        return 0.20; // Peligro bajo
    }
}
