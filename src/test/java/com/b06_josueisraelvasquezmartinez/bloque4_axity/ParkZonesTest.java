package com.b06_josueisraelvasquezmartinez.bloque4_axity;

import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.Tourist;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.model.TouristStatus;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.zone.ArrivalZone;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.zone.BathroomZone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ParkZonesTest {

    private ArrivalZone arrivalZone;
    private BathroomZone bathroomZone;
    private Random rng;

    @BeforeEach
    void setUp() {
        this.arrivalZone = new ArrivalZone();
        this.bathroomZone = new BathroomZone();
        this.rng = new Random(42);
    }

    @Test
    void testArrivalZone_ShouldProcessFIFOAndChargeTicket() {
        Tourist t1 = new Tourist(101, "Alan_Grant");
        Tourist t2 = new Tourist(102, "Ellie_Sattler");

        arrivalZone.enter(t1);
        arrivalZone.enter(t2);

        assertEquals(2, arrivalZone.getWaitingQueueSize());

        List<Tourist> batch = arrivalZone.processBatch(1);

        assertEquals(1, batch.size());
        assertEquals("Alan_Grant", batch.get(0).getName());
        assertEquals(TouristStatus.IN_PARK, batch.get(0).getStatus());
        assertTrue(batch.get(0).getMoneySpent() > 0);
        assertEquals(1, arrivalZone.getWaitingQueueSize());
    }

    @Test
    void testBathroomZone_ShouldEvacuateWhenTimeExpires() {
        Tourist tourist = new Tourist(201, "Ian_Malcolm");

        boolean entered = bathroomZone.tryEnter(tourist, this.rng);
        assertTrue(entered);
        assertEquals(1, bathroomZone.getCurrentOccupancy());

        // Avanzar los 3 pasos cronológicos requeridos para la expiración
        bathroomZone.tick();
        assertEquals(1, bathroomZone.getCurrentOccupancy());

        bathroomZone.tick();
        assertEquals(1, bathroomZone.getCurrentOccupancy());

        bathroomZone.tick();
        assertEquals(0, bathroomZone.getCurrentOccupancy(),
                "El turista debió ser desalojado automáticamente al expirar su tiempo.");
    }
}