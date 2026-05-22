package com.b06_josueisraelvasquezmartinez.bloque4_axity.controller;

import java.util.HashMap;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import com.b06_josueisraelvasquezmartinez.bloque4_axity.persistence.DatabaseService;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.persistence.EventRecord;
import com.b06_josueisraelvasquezmartinez.bloque4_axity.persistence.RevenueRecord;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ParkApiController {
    private final DatabaseService databaseService;

    public ParkApiController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    // GET http://localhost:8080/api/reports/revenues
    @GetMapping("/revenues")
    public List<RevenueRecord> getRevenuesReport() {
        return databaseService.getAllRevenues();
    }

    // GET http://localhost:8080/api/reports/events
    @GetMapping("/events")
    public List<EventRecord> getEventsReport() {
        return databaseService.getAllEvents();
    }

    // GET http://localhost:8080/api/reports/dashboard
    @GetMapping("/dashboard")
    public Map<String, Object> getFinancialDashboard() {
        List<RevenueRecord> revenues = databaseService.getAllRevenues();

        double totalRevenue = revenues.stream().mapToDouble(RevenueRecord::amount).sum();
        long totalSalesCount = revenues.size();

        Map<String, Object> summary = new HashMap<>();
        summary.put("status", "SUCCESS");
        summary.put("totalRevenueAudited", totalRevenue);
        summary.put("totalTransactionsCount", totalSalesCount);
        summary.put("databaseEngine", "H2_Relational");

        return summary;
    }
}
