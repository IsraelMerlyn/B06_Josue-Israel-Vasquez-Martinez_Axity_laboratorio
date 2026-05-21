package com.b06_josueisraelvasquezmartinez.bloque4_axity.model;

public class CarnivoreDinosaur extends Dinosaur {
    public CarnivoreDinosaur(int id, String name, String species) {
        // Los carnívoros consumen $500.0 diarios según el guión técnico
        super(id, name, species, 500.0);
    }

    @Override
    public String getDiet() {
        return "CARNIVORE";
    }

    @Override
    public double getDangerLevel() {
        return 0.95; // Alto peligro de ataque
    }
}
