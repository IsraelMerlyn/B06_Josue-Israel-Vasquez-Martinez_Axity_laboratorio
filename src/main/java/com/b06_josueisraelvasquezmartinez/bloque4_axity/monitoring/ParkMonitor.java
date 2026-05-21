package com.b06_josueisraelvasquezmartinez.bloque4_axity.monitoring;

import com.b06_josueisraelvasquezmartinez.bloque4_axity.simulation.ParkState;

public final class ParkMonitor {
    // Constructor privado para impedir que se creen instancias
    private ParkMonitor() {
    }

    /**
     * Imprime en consola un snapshot detallado con las 5 métricas requeridas por el
     * laboratorio
     * 
     * @param state            Estado global con los datos vivos del parque
     * @param energyPercentage Nivel de energía actual leído de la planta
     * @param plantOperational Estado operativo de la red eléctrica
     */
    public static void displaySnapshot(ParkState state, double energyPercentage, boolean plantOperational) {
        if (state == null)
            return;

        System.out.println("\n=================================================================");
        System.out.printf("TABLERO DE MONITOREO - PASO DE SIMULACIÓN: [%d]\n", state.getCurrentStep());
        System.out.println("=================================================================");

        // Métrica 1: Turistas activos en las instalaciones
        System.out.printf("• [Métrica 1] Turistas Activos dentro del parque : %d\n", state.countActiveTourists());

        // Métrica 2: Dinosaurios resguardados de forma segura
        System.out.printf("• [Métrica 2] Dinosaurios seguros en encierros   : %d / %d\n",
                state.countDinosaursInEnclosure(), state.getAllDinosaurs().size());

        // Métrica 3: Energía disponible en porcentaje y su estado de salud
        String estatusRed = plantOperational ? "⚡ OPERACIONAL" : "🚨 APAGÓN CRÍTICO";
        System.out.printf("• [Métrica 3] Nivel de Energía Eléctrica         : %.2f%% (%s)\n",
                energyPercentage, estatusRed);

        // Métrica 4: Eventos dinámicos o contingencias activas en el paso de ejecución
        // actual
        String eventos = state.getActiveEventNames().isEmpty()
                ? "Ninguno (Operación normal)"
                : String.join(", ", state.getActiveEventNames());
        System.out.printf("• [Métrica 4] Contingencias / Eventos Activos   : [%s]\n", eventos);

        // Métrica 5: Vehículos fuera de servicio (ya sea por mantenimiento o por
        // averías)
        System.out.printf("• [Métrica 5] Vehículos Fuera de Servicio (Uso/Rotos): %d / %d\n",
                state.countUnavailableVehicles(), state.getVehicles().size());

        System.out.println("-----------------------------------------------------------------");
        System.out.printf(" Balance Financiero Temporal -> Ingresos: $%.2f | Gastos: $%.2f\n",
                state.getTotalRevenue(), state.getTotalExpenses());
        System.out.println("=================================================================\n");
    }
}
