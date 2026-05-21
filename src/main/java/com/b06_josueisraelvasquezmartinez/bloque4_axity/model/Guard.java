package com.b06_josueisraelvasquezmartinez.bloque4_axity.model;

import java.util.List;

public class Guard extends Worker {
    public Guard(int id, String name, double dailySalary) {
        super(id, name, dailySalary);
    }

    @Override
    public String getRole() {
        return "GUARD";
    }

    // Lógica operativa: Busca dinosaurios escapados y los regresa al corral
    public void recaptureEscapedDinosaurs(List<Dinosaur> dinosaurs) {
        if (dinosaurs == null)
            return;
        for (Dinosaur dino : dinosaurs) {
            if (dino.getStatus() == DinosaurStatus.ESCAPED) {
                dino.returnToEnclosure();
                System.out.println(
                        " El Guardia [" + getName() + "] recapturó con éxito al dinosaurio: " + dino.getName());
            }
        }
    }
}
