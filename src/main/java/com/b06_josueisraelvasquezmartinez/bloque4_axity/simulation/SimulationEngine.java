package com.b06_josueisraelvasquezmartinez.bloque4_axity.simulation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.config.ParkConfig;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.event.BlackoutEvent;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.event.DealsHourEvent;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.event.DinosaurEscapeEvent;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.event.SimulationEvent;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.event.StormEvent;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.event.VehicleFailureEvent;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.CarnivoreDinosaur;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.Guard;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.HerbivoreDinosaur;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.Technician;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.Tourist;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.TouristStatus;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.Vehicle;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.Worker;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.monitoring.ParkMonitor;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.persistence.DatabaseService;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.persistence.ExpenseRecord;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.persistence.RevenueRecord;
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
    private final DatabaseService databaseService;

    // Catálogo de Estrategias de Eventos (Pattern Strategy)
    private final List<SimulationEvent> playlistEvents;

    // Elementos operativos de las Zonas
    private ArrivalZone arrivalZone;
    private CentralHub centralHub;
    private BathroomZone bathroomZone;
    private PowerPlant powerPlant;
    private final List<ObservationEnclosure> enclosures;

    // Parámetros core de control
    private int totalSteps;
    private int batchSize;
    private int monitoringInterval;
    private double workerSalary;

    public SimulationEngine(DatabaseService databaseService) {
        this.databaseService = databaseService;
        this.rng = new Random();
        this.enclosures = new ArrayList<>();
        this.playlistEvents = new ArrayList<>();
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(" Cargando infraestructura transaccional y catálogo de contingencias...");
        initSimulation();

        System.out.println("Ejecutando simulación dinámica con persistencia activa...");

        for (int step = 0; step < this.totalSteps; step++) {
            this.state.incrementStep();

            // Limpiar estados efímeros del paso anterior
            this.state.clearActiveEventsForStep();

            List<Tourist> newInPark = this.arrivalZone.processBatch(this.batchSize);
            for (Tourist t : newInPark) {
                double ticketPrice = 25.0;
                this.state.addRevenue(ticketPrice);

                databaseService.appendRevenue(new RevenueRecord(
                        0, "BOLETO", ticketPrice, t.getId(), this.arrivalZone.getName(), LocalDateTime.now()));
                this.state.getAllTourists().add(t);
            }

            for (SimulationEvent event : this.playlistEvents) {
                if (this.rng.nextDouble() < event.getProbability()) {
                    // Detonar el evento sobre la infraestructura
                    event.execute(this.state, this.rng);

                    // Persistir la contingencia en la base de datos H2
                    databaseService.appendEvent(event.toRecord(
                            this.state.getCurrentStep(), "Operación de Campo Ecosistema"));
                }
            }

            for (Tourist tourist : this.state.getAllTourists()) {
                if (tourist.getStatus() == TouristStatus.IN_PARK) {

                    double cashBefore = tourist.getMoneySpent();
                    this.centralHub.visit(tourist, this.rng);
                    double cashAfter = tourist.getMoneySpent();

                    if (cashAfter > cashBefore) {
                        double spentAmount = cashAfter - cashBefore;

                        // Aplicar impacto comercial: Si hay ofertas, el parque descuenta el 20% en caja
                        if (this.state.isDealsHourActive()) {
                            spentAmount = spentAmount * (1.0 - this.state.getCurrentDiscount());
                        }

                        this.state.addRevenue(spentAmount);
                        databaseService.appendRevenue(new RevenueRecord(
                                0, "SOUVENIR", spentAmount, tourist.getId(), this.centralHub.getName(),
                                LocalDateTime.now()));
                    }

                    // Intento de ingreso a baños y SPA
                    if (this.rng.nextDouble() < 0.25) {
                        double cashBeforeBath = tourist.getMoneySpent();
                        boolean entered = this.bathroomZone.tryEnter(tourist, this.rng);
                        double cashAfterBath = tourist.getMoneySpent();

                        if (entered && (cashAfterBath > cashBeforeBath)) {
                            double spaAmount = cashAfterBath - cashBeforeBath;
                            this.state.addRevenue(spaAmount);
                            databaseService.appendRevenue(new RevenueRecord(
                                    0, "SPA", spaAmount, tourist.getId(), this.bathroomZone.getName(),
                                    LocalDateTime.now()));
                        }
                    }
                }
            }

            this.bathroomZone.tick();
            this.powerPlant.tick(this.rng);
            this.state.getVehicles().forEach(Vehicle::tick);

            // Nómina de empleados corriente por paso
            for (Worker worker : this.state.getAllWorkers()) {
                this.state.addExpense(this.workerSalary);
                databaseService.appendExpense(new ExpenseRecord(
                        0, "SALARIOS", this.workerSalary,
                        "Nómina de operación: " + worker.getName(),
                        LocalDateTime.now()));
            }

            for (Worker worker : this.state.getAllWorkers()) {
                if (worker instanceof Guard guard) {
                    // Los guardias buscan dinosaurios sueltos y los recapturan
                    guard.recaptureEscapedDinosaurs(this.state.getAllDinosaurs());
                } else if (worker instanceof Technician technician) {
                    // Los técnicos buscan camiones libres para reparar cortocircuitos
                    technician.repairIfNeeded(this.powerPlant, this.state.getVehicles());
                }
            }

            if (this.state.getCurrentStep() % this.monitoringInterval == 0) {
                ParkMonitor.displaySnapshot(
                        this.state,
                        this.powerPlant.getCurrentEnergy(),
                        this.powerPlant.isOperational());
            }
        }

        System.out.println("\n Simulación finalizada por completo. Registro de auditoría guardado con éxito.");
    }

    private void initSimulation() {
        ParkConfig config = ParkConfig.getInstance();
        this.state = new ParkState(this.rng);
        this.totalSteps = config.getInt("simulation.totalSteps", 100);
        this.batchSize = config.getInt("simulation.arrivalBatchSize", 5);
        this.monitoringInterval = config.getInt("monitoring.intervalSteps", 10);
        this.workerSalary = config.getDouble("simulation.workers.dailySalary", 150.0);

        // Inicializar Zonas Físicas
        this.arrivalZone = new ArrivalZone();
        this.centralHub = new CentralHub();
        this.bathroomZone = new BathroomZone();
        this.powerPlant = new PowerPlant();

        // Enlazar la planta eléctrica al estado centralizador
        this.state.setPowerPlant(this.powerPlant);

        // Registrar el pool de estrategias de eventos
        this.playlistEvents.add(new DinosaurEscapeEvent());
        this.playlistEvents.add(new BlackoutEvent());
        this.playlistEvents.add(new StormEvent());
        this.playlistEvents.add(new DealsHourEvent());
        this.playlistEvents.add(new VehicleFailureEvent());

        // Cargar Catálogos Iniciales (Vehículos, Turistas, Dinosaurios y Trabajadores)
        int totalVehicles = config.getInt("vehicles.count", 4);
        for (int i = 1; i <= totalVehicles; i++) {
            this.state.getVehicles().add(new Vehicle(i));
        }

        int totalTourists = config.getInt("simulation.tourists", 50);
        for (int i = 1; i <= totalTourists; i++) {
            this.arrivalZone.enter(new Tourist(i, "Visitante_" + i));
        }

        int carnivoresCount = config.getInt("simulation.dinosaurs.carnivores", 5);
        int herbivoresCount = config.getInt("simulation.dinosaurs.herbivores", 15);
        int dinoId = 1;

        for (int i = 1; i <= carnivoresCount; i++) {
            this.state.getAllDinosaurs().add(new CarnivoreDinosaur(dinoId++, "Raptor_" + i, "Velociraptor"));
        }
        for (int i = 1; i <= herbivoresCount; i++) {
            this.state.getAllDinosaurs().add(new HerbivoreDinosaur(dinoId++, "Bronto_" + i, "Brontosaurio"));
        }

        int guardsCount = config.getInt("simulation.workers.guards", 3);
        int techsCount = config.getInt("simulation.workers.technicians", 2);
        int workerId = 1;

        for (int i = 1; i <= guardsCount; i++) {
            this.state.getAllWorkers().add(new Guard(workerId++, "Guardia_" + i, this.workerSalary));
        }
        for (int i = 1; i <= techsCount; i++) {
            this.state.getAllWorkers().add(new Technician(workerId++, "Técnico_" + i, this.workerSalary));
        }
    }
}