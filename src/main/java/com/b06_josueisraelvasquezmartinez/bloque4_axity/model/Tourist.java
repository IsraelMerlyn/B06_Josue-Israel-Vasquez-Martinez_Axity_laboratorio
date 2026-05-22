package com.b06_josueisraelvasquezmartinez.bloque4_axity.model;

import lombok.Getter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class Tourist {
    private final int id;
    private final String name;
    private TouristStatus status;
    private double moneySpent;
    private final List<String> visitedZones;

    // Constructor que inicializa al turista en el punto de entrada
    public Tourist(int id, String name) {
        this.id = id;
        this.name = name;
        this.status = TouristStatus.WAITING;
        this.moneySpent = 0.0;
        this.visitedZones = new ArrayList<>();
    }

    // Permite actualizar el estado del turista en el ciclo del parque
    public void changeStatus(TouristStatus newStatus) {
        if (this.status != TouristStatus.EXITED) {
            this.status = newStatus;
        }
    }

    // Método de negocio: Acumula los gastos de forma segura e incremental
    public void spend(double amount) {
        if (amount > 0) {
            this.moneySpent += amount;
        }
    }

    // Método de negocio: Registra la bitácora de zonas que ha visitado
    public void recordVisit(String zoneName) {
        if (zoneName != null && !zoneName.isBlank()) {
            this.visitedZones.add(zoneName);
        }
    }

    // Metodo para obtener la lista de la zona visitada
    public List<String> getVisitedZones() {
        return Collections.unmodifiableList(visitedZones);
    }
}
