package com.b06_josueisraelvasquezmartinez.bloque4_axity.model;

import lombok.Getter;

@Getter
public abstract class Worker {
    private final int id;
    private final String name;
    private final double dailySalary;

    public Worker(int id, String name, double dailySalary) {
        this.id = id;
        this.name = name;
        this.dailySalary = dailySalary;
    }

    public abstract String getRole(); // Retorna "GUARD" o "TECHNICIAN"
}
