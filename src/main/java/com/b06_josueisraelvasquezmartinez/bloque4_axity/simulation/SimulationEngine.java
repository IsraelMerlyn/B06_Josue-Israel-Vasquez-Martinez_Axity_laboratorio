package com.b06_josueisraelvasquezmartinez.bloque4_axity.simulation;

import java.util.ArrayList;
import java.util.Random;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.config.ParkConfig;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.CarnivoreDinosaur;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.Guard;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.HerbivoreDinosaur;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.Technician;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.Tourist;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.TouristStatus;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.Vehicle;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.monitoring.ParkMonitor;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.zone.ArrivalZone;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.zone.BathroomZone;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.zone.CentralHub;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.zone.ExperienceType;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.zone.ObservationEnclosure;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.zone.PowerPlant;

@Component
public class SimulationEngine implements CommandLineRunner {
    private ParkState state;
    private final Random rng;

    // Elementos operativos del Parque
    private ArrivalZone arrivalZone;
    private CentralHub centralHub;
    private BathroomZone bathroomZone;
    private PowerPlant powerPlant;
    private final List<ObservationEnclosure> enclosures;

    // Parámetros de control de la simulación
    private int totalSteps;
    private int batchSize;
    private int monitoringInterval;

    public SimulationEngine() {
        this.rng = new Random(); // Lab Intermedio: Semilla aleatoria (No-determinista)
        this.enclosures = new ArrayList<>();
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("⚙️ [Engine] Configurando parámetros operativos del parque...");
        initSimulation();

        System.out.println("🚀 [Engine] Iniciando bucle cronológico de la simulación...");

        for (int step = 0; step < this.totalSteps; step++) {
            this.state.incrementStep();

            // limpiamos banderas temporales
            this.state.clearActiveEventsForStep();

            // ARRIBO Y PROCESAMIENTO DE TURISTAS POR LOTES
            List<Tourist> newInPark = this.arrivalZone.processBatch(this.batchSize);
            for (Tourist t : newInPark) {
                this.state.addRevenue(25.0); // Sumar tarifa del boleto de entrada al balance global
                this.state.getAllTourists().add(t);
            }

            // DINÁMICA SECUENCIAL DE MOVIMIENTO
            for (Tourist tourist : this.state.getAllTourists()) {
                if (tourist.getStatus() == TouristStatus.IN_PARK) {
                    this.centralHub.visit(tourist, this.rng);

                    // Decisión inteligente: Con un 30% de probabilidad intentan ir a los baños
                    if (this.rng.nextDouble() < 0.30) {
                        this.bathroomZone.tryEnter(tourist, this.rng);
                    }
                }
            }

            // TICKS DE ZONAS Y VEHÍCULOS
            this.bathroomZone.tick();
            this.powerPlant.tick(this.rng);
            this.state.getVehicles().forEach(Vehicle::tick);

            // MONITOREO CONDICIONAL POR INTERVALOS
            if (this.state.getCurrentStep() % this.monitoringInterval == 0) {
                ParkMonitor.displaySnapshot(
                        this.state,
                        this.powerPlant.getCurrentEnergy(),
                        this.powerPlant.isOperational());
            }
        }

        System.out.println("\n🏁 Simulación completada con éxito tras " + this.totalSteps + " pasos.");
    }

    /**
     * Carga las propiedades del archivo de configuración e inyecta la población
     * inicial al estado
     */
    private void initSimulation() {
        ParkConfig config = ParkConfig.getInstance();
        this.state = new ParkState(this.rng);

        // Mapear variables del loop
        this.totalSteps = config.getInt("simulation.totalSteps", 100);
        this.batchSize = config.getInt("simulation.arrivalBatchSize", 5);
        this.monitoringInterval = config.getInt("monitoring.intervalSteps", 10);

        // Instanciar zonas base
        this.arrivalZone = new ArrivalZone();
        this.centralHub = new CentralHub();
        this.bathroomZone = new BathroomZone();
        this.powerPlant = new PowerPlant();

        // Registrar los 3 encierros requeridos por el laboratorio técnico
        this.enclosures.add(new ObservationEnclosure("Valle de Triceratops", ExperienceType.BASIC));
        this.enclosures.add(new ObservationEnclosure("Aviario de Pterodáctilos", ExperienceType.PREMIUM));
        this.enclosures.add(new ObservationEnclosure("Fosa del T-Rex", ExperienceType.VIP));

        // Inyectar Vehículos al taller central según la configuración
        int totalVehicles = config.getInt("vehicles.count", 4);
        for (int i = 1; i <= totalVehicles; i++) {
            this.state.getVehicles().add(new Vehicle(i));
        }

        // Cargar Turistas Iniciales formados en el muelle de abordaje
        int totalTourists = config.getInt("simulation.tourists", 50);
        for (int i = 1; i <= totalTourists; i++) {
            this.arrivalZone.enter(new Tourist(i, "Visitante_" + i));
        }

        // Poblar el catálogo biológico del parque (Dinosaurios)
        int carnivoresCount = config.getInt("simulation.dinosaurs.carnivores", 5);
        int herbivoresCount = config.getInt("simulation.dinosaurs.herbivores", 15);
        int dinoId = 1;

        for (int i = 1; i <= carnivoresCount; i++) {
            this.state.getAllDinosaurs().add(new CarnivoreDinosaur(dinoId++, "Raptor_" + i, "Velociraptor"));
        }
        for (int i = 1; i <= herbivoresCount; i++) {
            this.state.getAllDinosaurs().add(new HerbivoreDinosaur(dinoId++, "Bronto_" + i, "Brontosaurio"));
        }

        // Contratar la fuerza trabajadora del parque
        double salary = config.getDouble("simulation.workers.dailySalary", 150.0);
        int guardsCount = config.getInt("simulation.workers.guards", 3);
        int techsCount = config.getInt("simulation.workers.technicians", 2);
        int workerId = 1;

        for (int i = 1; i <= guardsCount; i++) {
            this.state.getAllWorkers().add(new Guard(workerId++, "Guardia_" + i, salary));
        }
        for (int i = 1; i <= techsCount; i++) {
            this.state.getAllWorkers().add(new Technician(workerId++, "Técnico_" + i, salary));
        }
    }
}
