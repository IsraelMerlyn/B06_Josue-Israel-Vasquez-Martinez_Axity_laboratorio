package com.b06_josueisraelvasquezmartinez.bloque4_axity.zone;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.b06_josueisraelvasquezmartinez.bloque4_axity.config.ParkConfig;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.SatisfactionSurvey;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.Tourist;

public class ObservationEnclosure implements ParkZone {
    private final String name;
    private final ExperienceType experienceType;
    private final int maxCapacity;
    private final double entryFee;

    // Conjunto de turistas actualmente observando el encierro
    private final Set<Tourist> currentVisitors;

    public ObservationEnclosure(String name, ExperienceType experienceType) {
        ParkConfig config = ParkConfig.getInstance();
        this.name = name;
        this.experienceType = experienceType;
        this.currentVisitors = new HashSet<>();

        // Mapeo dinámico de propiedades según el tipo de experiencia provisto
        String keyPrefix = "enclosure." + experienceType.name().toLowerCase();
        this.maxCapacity = config.getInt(keyPrefix + ".maxVisitors", 10);
        this.entryFee = config.getDouble(keyPrefix + ".entryFee", 15.0);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean hasCapacity() {
        return this.currentVisitors.size() < this.maxCapacity;
    }

    @Override
    public int getCurrentOccupancy() {
        return this.currentVisitors.size();
    }

    @Override
    public int getMaxCapacity() {
        return this.maxCapacity;
    }

    @Override
    public void enter(Tourist tourist) {
        if (tourist != null && hasCapacity()) {
            this.currentVisitors.add(tourist);
            tourist.recordVisit(this.name);
        }
    }

    @Override
    public void exit(Tourist tourist) {
        if (tourist != null) {
            this.currentVisitors.remove(tourist);
        }
    }

    /**
     * Ejecuta la experiencia de visita del turista al encierro, cobrando su tarifa
     * 
     * @param tourist El visitante activo
     * @param rng     Generador aleatorio unificado para la encuesta
     * @return Un registro inmutable SatisfactionSurvey listo para auditar
     */
    public SatisfactionSurvey visit(Tourist tourist, Random rng) {
        if (tourist == null || rng == null)
            return null;

        // Cobranza obligatoria por derecho de visibilidad
        tourist.spend(this.entryFee);
        System.out.println("🦕 El Turista [" + tourist.getName() + "] pagó $" + this.entryFee
                + " por entrar al encierro: " + this.name);

        // Generación automática de la encuesta de satisfacción amarrada al tipo de

        return conductSurvey(tourist, rng);
    }

    private SatisfactionSurvey conductSurvey(Tourist tourist, Random rng) {
        int score = switch (this.experienceType) {
            case BASIC -> rng.nextInt(3) + 1;
            case PREMIUM -> rng.nextInt(3) + 2;
            case VIP -> rng.nextInt(3) + 3;
        };

        System.out.println("📊 Encuesta generada: [" + tourist.getName() + "] calificó con " + score + " estrellas a "
                + this.name);
        return new SatisfactionSurvey(tourist.getId(), this.name, score);
    }

    public ExperienceType getExperienceType() {
        return this.experienceType;
    }
}
