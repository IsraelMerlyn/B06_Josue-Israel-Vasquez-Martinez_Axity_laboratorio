package com.b06_josueisraelvasquezmartinez.bloque4_axity.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ParkConfig {
    private static ParkConfig instance;
    private final Properties props;

    // Constructor
    private ParkConfig() {
        props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.err.println("⚠️ Alerta: No se encontró application.properties en el classpath.");
            } else {
                props.load(input);
            }
        } catch (IOException ex) {
            System.err.println("❌ Error crítico cargando la configuración del parque: " + ex.getMessage());
        }
    }

    // Hilos seguros usando synchronized
    public static synchronized ParkConfig getInstance() {
        if (instance == null) {
            instance = new ParkConfig();
        }
        return instance;
    }

    // Métodos de lectura por defecto
    public String getString(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        String value = props.getProperty(key);
        if (value == null)
            return defaultValue;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public double getDouble(String key, double defaultValue) {
        String value = props.getProperty(key);
        if (value == null)
            return defaultValue;
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public long getSeed() {
        return getInt("simulation.seed", 42);
    }

    public int getTotalSteps() {
        return getInt("simulation.totalSteps", 100);
    }

    // Método para las pruebas unitarias
    public static void resetForTesting() {
        synchronized (ParkConfig.class) {
            instance = null;
        }
    }
}
