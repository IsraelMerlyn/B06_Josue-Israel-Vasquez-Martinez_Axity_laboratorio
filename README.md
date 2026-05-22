#  Ecosistema Digital de Monitoreo y Simulación - Parque Jurásico (Bloque 4)

Este proyecto implementa un motor de simulación probabilística y transaccional para la gestión operativa y de seguridad de un parque de dinosaurios. Desarrollado en **Java 21** y **Spring Boot**, el sistema aplica principios de arquitectura limpia, patrones de diseño de software y persistencia relacional automatizada.

**Autor:** Josue Israel Vasquez Martinez (*Israel Merlyn*)  
**Rol:** Ssr. Software Engineer & Technical Lead / University Tech Lecturer

---

##  Arquitectura y Patrones de Diseño

El sistema está diseñado bajo el enfoque de **Arquitectura Limpia (Clean Architecture)**, separando de forma estricta el dominio de negocio de los detalles de infraestructura (base de datos y controladores web):

*   **Pattern Strategy (Motor de Eventos):** Permite inyectar de forma dinámica contingencias biológicas, mecánicas o ambientales (`SimulationEvent`) al bucle principal del parque sin acoplar el código.
*   **Pattern Singleton:** Utilizado en `ParkConfig` para centralizar la lectura de propiedades de configuración del parque de forma unificada.
*   **State Holder:** La clase `ParkState` encapsula el estado vivo y expone métricas puras en caliente, aislando la mutación de datos del monitor visual.
*   **Inyección de Dependencias (IoC):** Todo el ciclo de vida del motor e infraestructura de persistencia es administrado de manera nativa por el contenedor de Spring.

---

##  Estructura del Proyecto

```text
src/
├── main/
│   ├── java/com/b06_josueisraelvasquezmartinez/bloque4_axity/
│   │   ├── config/         # Configuración global (Singleton)
│   │   ├── model/          # Modelos de dominio (Dinosaurios, Turistas, Trabajadores)
│   │   ├── zone/           # Zonas físicas del parque bajo contrato ParkZone
│   │   ├── simulation/     # Estado global y Motor (CommandLineRunner)
│   │   ├── event/          # Catálogo de contingencias (Strategy)
│   │   ├── monitoring/     # Tablero de control y renderizado visual
│   │   ├── persistence/    # Records inmutables y repositorio JDBC seguro
│   │   └── controller/     # Capa REST expuesta al exterior
│   └── resources/
│       ├── application.properties    # Parámetros operativos del parque
│       └── db/changelog/             # Control de versiones de base de datos (Liquibase)
└── test/                             # Suite de pruebas unitarias automatizadas (JUnit 5)
```

---

##  Infraestructura de Persistencia (Liquibase + H2)

El proyecto utiliza **Liquibase** como herramienta de migración estructural. Las tablas se autogeneran al arrancar en la base de datos relacional **H2** (en memoria). Toda manipulación de datos se ejecuta mediante `PreparedStatement` para mitigar vulnerabilidades de inyección SQL.

### Modelado de Tablas de Auditoría
*   **`revenues`:** Registra la venta de boletos, souvenirs y membresías de SPA correlacionadas al ID del turista y zona.
*   **`expenses`:** Registra el pago por ciclos de nómina a la fuerza laboral y penalizaciones por siniestros.
*   **`events`:** Bitácora histórica de contingencias detonadas probabilísticamente en el parque.

---

##  Endpoints REST Expuestos (API Reportes)

Una vez que la simulación finaliza sus pasos configurados, el servidor web permanece activo en el puerto `8085`. Puedes consultar las métricas desde cualquier cliente HTTP o navegador web a través de las siguientes rutas:

| Tipo | Endpoint | Descripción | Formato de Respuesta |
| :--- | :--- | :--- | :--- |
| **GET** | `/api/reports/dashboard` | Estado financiero consolidado y KPIs de transacciones. | `JSON Object` |
| **GET** | `/api/reports/revenues` | Historial cronológico detallado de todos los ingresos. | `JSON Array` |
| **GET** | `/api/reports/events` | Bitácora de emergencias y afectaciones de campo. | `JSON Array` |

---

##  Compilación, Pruebas y Ejecución

### Requisitos Previos
*   **Java Development Kit (JDK) 21**
*   **Apache Maven 3.9+**

### 1. Compilar el proyecto
Para compilar el código fuente y verificar la sintaxis del proyecto sin ejecutar la simulación:
```bash
mvn clean compile
```

### 2. Ejecutar la Suite de Pruebas Unitarias
Para correr las pruebas automatizadas con **JUnit 5** y validar el comportamiento de las colas FIFO y los desalojos de aforo por tiempo:
```bash
mvn clean test
```

### 3. Levantar la Simulación y el Servidor Web
Para arrancar el motor y comenzar a auditar las transacciones en tiempo real:
```bash
mvn clean spring-boot:run
```

---

## 📊 Métricas de Control del Monitor

El componente `ParkMonitor` imprime en consola cada intervalo de pasos configurado un panel con las **5 métricas core obligatorias**:
1.  **Métrica 1:** Conteo de turistas activos dentro de las instalaciones.
2.  **Métrica 2:** Dinosaurios seguros bajo resguardo en encierros vs población total.
3.  **Métrica 3:** Nivel porcentual de energía eléctrica y estatus operativo de la planta.
4.  **Métrica 4:** Lista de contingencias o eventos comerciales activos en el paso actual.
5.  **Métrica 5:** Cantidad de vehículos fuera de servicio (en uso por de mitigación o averiados).
```