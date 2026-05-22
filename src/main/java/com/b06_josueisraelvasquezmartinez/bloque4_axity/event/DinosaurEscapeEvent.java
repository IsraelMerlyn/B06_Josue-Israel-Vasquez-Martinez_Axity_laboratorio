package com.b06_josueisraelvasquezmartinez.bloque4_axity.event;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.List;

import com.b06_josueisraelvasquezmartinez.bloque4_axity.config.ParkConfig;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.Dinosaur;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.DinosaurStatus;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.Tourist;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.TouristStatus;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.persistence.EventRecord;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.simulation.ParkState;

public class DinosaurEscapeEvent implements SimulationEvent {
    private final String name;
    private final String description;
    private final double probability;

    public DinosaurEscapeEvent() {
        ParkConfig config = ParkConfig.getInstance();
        this.name = "ESCAPE_DINOSAURIO";
        this.description = "Falla de contención perimetral. Un espécimen rompe la cerca de seguridad.";
        this.probability = config.getDouble("event.escape.probability", 0.05);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public double getProbability() {
        return this.probability;
    }

    @Override
    public void execute(ParkState state, Random rng) {
        if (state == null || rng == null)
            return;

        // Filtrar dinosaurios que sigan resguardados
        List<Dinosaur> safeDinos = state.getAllDinosaurs().stream()
                .filter(d -> d.getStatus() == DinosaurStatus.IN_ENCLOSURE)
                .toList();

        if (safeDinos.isEmpty())
            return; // Si todos están sueltos, no hay nuevos escapes

        // Seleccionar uno al azar para desatar el caos
        Dinosaur escapedDino = safeDinos.get(rng.nextInt(safeDinos.size()));
        escapedDino.escape();

        String affectedEntities = "Dinosaurio: " + escapedDino.getName() + " (" + escapedDino.getSpecies() + ")";
        System.err.println(" ¡El " + escapedDino.getSpecies() + " llamado ["
                + escapedDino.getName() + "] se ha escapado!");

        // Evaluar potencial ataque a un turista activo dentro del parque
        List<Tourist> activeTourists = state.getAllTourists().stream()
                .filter(t -> t.getStatus() == TouristStatus.IN_PARK)
                .toList();

        if (!activeTourists.isEmpty() && rng.nextDouble() < escapedDino.getDangerLevel()) {
            Tourist victim = activeTourists.get(rng.nextInt(activeTourists.size()));
            victim.changeStatus(TouristStatus.ATTACKED);
            affectedEntities += " | Turista Atacado: " + victim.getName();
            System.err.println(" ¡El espécimen suelto embistió al visitante [" + victim.getName() + "]!");
        }

        // Registrar el nombre del evento en las banderas del paso para el monitor
        state.registerActiveEvent(this.name);
    }

    @Override
    public EventRecord toRecord(long step, String affectedEntities) {
        return new EventRecord(0, step, this.name, this.description, affectedEntities, LocalDateTime.now());
    }
}
