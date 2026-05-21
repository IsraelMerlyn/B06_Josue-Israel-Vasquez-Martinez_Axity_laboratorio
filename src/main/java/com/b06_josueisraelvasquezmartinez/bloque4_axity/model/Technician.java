package com.b06_josueisraelvasquezmartinez.bloque4_axity.model;

import lombok.Getter;

@Getter
public class Technician extends Worker {
    public Technician(int id, String name, double dailySalary) {
        super(id, name, dailySalary);
    }

    @Override
    public String getRole() {
        return "TECHNICIAN";
    }
}
