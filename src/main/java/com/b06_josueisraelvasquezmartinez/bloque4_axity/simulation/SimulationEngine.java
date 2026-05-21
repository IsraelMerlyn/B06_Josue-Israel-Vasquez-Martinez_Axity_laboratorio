package com.b06_josueisraelvasquezmartinez.bloque4_axity.simulation;

import java.time.LocalDateTime;
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
    private double workerSalary;

    // Spring inyecta de forma automática el DatabaseService en el constructor
    public SimulationEngine(DatabaseService databaseService) {
        this.databaseService = databaseService;
        this.rng = new Random();
        this.enclosures = new ArrayList<>();
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("  Inicializando parámetros operativos y base de datos...");
        initSimulation();

        System.out.println(" Iniciando bucle cronológico transaccional...");

        for (int step = 0; step < this.totalSteps; step++) {
            this.state.incrementStep();
            this.state.clearActiveEventsForStep();

            // PERSISTENCIA DE BOLETOS
            List<Tourist> newInPark = this.arrivalZone.processBatch(this.batchSize);
            for (Tourist t : newInPark) {
                double ticketPrice = 25.0;
                this.state.addRevenue(ticketPrice);

                // Registrar ingreso en Base de Datos
                databaseService.appendRevenue(new RevenueRecord(
                        0, "BOLETO", ticketPrice, t.getId(), this.arrivalZone.getName(), LocalDateTime.now()));
                this.state.getAllTourists().add(t);
            }

            // DINÁMICA DE MOVIMIENTO INTERNO Y SOUVENIRS
            for (Tourist tourist : this.state.getAllTourists()) {
                if (tourist.getStatus() == TouristStatus.IN_PARK) {

                    // Al visitar el Hub, guardamos el saldo antes de la interacción
                    double cashBefore = tourist.getMoneySpent();
                    this.centralHub.visit(tourist, this.rng);
                    double cashAfter = tourist.getMoneySpent();

                    // Si el saldo aumentó, significa que el turista ejecutó una compra de souvenir
                    if (cashAfter > cashBefore) {
                        double spentAmount = cashAfter - cashBefore;
                        this.state.addRevenue(spentAmount);
                        databaseService.appendRevenue(new RevenueRecord(
                                0, "SOUVENIR", spentAmount, tourist.getId(), this.centralHub.getName(),
                                LocalDateTime.now()));
                    }

                    // Interacción pseudoaleatoria con la zona de baños y potencial SPA
                    if (this.rng.nextDouble() < 0.30) {
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

            // ACTUALIZACIÓN DE INFRAESTRUCTURA Y GASTOS OPERATIVOS
            this.bathroomZone.tick();
            this.powerPlant.tick(this.rng);
            this.state.getVehicles().forEach(Vehicle::tick);

            // Registro contable de salarios de la fuerza laboral
            for (Worker worker : this.state.getAllWorkers()) {
                this.state.addExpense(this.workerSalary);
                databaseService.appendExpense(new ExpenseRecord(
                        0, "SALARIOS", this.workerSalary,
                        "Pago de nómina por ciclo al empleado: " + worker.getName() + " (" + worker.getRole() + ")",
                        LocalDateTime.now()));
            }

            // TABLERO DE MONITOREO
            if (this.state.getCurrentStep() % this.monitoringInterval == 0) {
                ParkMonitor.displaySnapshot(
                        this.state,
                        this.powerPlant.getCurrentEnergy(),
                        this.powerPlant.isOperational());
            }
        }

        System.out.println("\n Simulación finalizada. Todos los movimientos fueron resguardados en H2 Database.");
    }

    private void initSimulation() {
        ParkConfig config = ParkConfig.getInstance();
        this.state = new ParkState(this.rng);

        this.totalSteps = config.getInt("simulation.totalSteps", 100);
        this.batchSize = config.getInt("simulation.arrivalBatchSize", 5);
        this.monitoringInterval = config.getInt("monitoring.intervalSteps", 10);
        this.workerSalary = config.getDouble("simulation.workers.dailySalary", 150.0);

        this.arrivalZone = new ArrivalZone();
        this.centralHub = new CentralHub();
        this.bathroomZone = new BathroomZone();
        this.powerPlant = new PowerPlant();

        this.enclosures.add(new ObservationEnclosure("Valle de Triceratops", ExperienceType.BASIC));
        this.enclosures.add(new ObservationEnclosure("Aviario de Pterodáctilos", ExperienceType.PREMIUM));
        this.enclosures.add(new ObservationEnclosure("Fosa del T-Rex", ExperienceType.VIP));

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
