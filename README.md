# KafkaWars: The Event Sourced Battlefield

Este proyecto es una implementación de un motor de juego de estrategia por turnos basado en una arquitectura de Event Sourcing y CQRS, utilizando Apache Kafka como la única fuente de verdad para el estado del juego.

La arquitectura está diseñada para ser altamente escalable, segura y resistente a fallos, siguiendo patrones de sistemas distribuidos modernos.

## Stack Tecnológico

*   **Lenguaje:** Java 21
*   **Framework:** Spring Boot 3
*   **Mensajería:** Apache Kafka
*   **Protocolo de Comandos:** HTTP (REST)
*   **Protocolo de Estado:** WebSockets (conceptual)
*   **Seguridad:** Firmas criptográficas de comandos (conceptual)
*   **Build Tool:** Apache Maven
*   **Infraestructura:** Docker Compose

## Prerrequisitos

Asegúrate de tener instalados los siguientes programas en tu sistema:

*   **Java 21** (o superior)
*   **Apache Maven**
*   **Docker** y **Docker Compose**

---

## Cómo Ejecutar (Entorno de Desarrollo Local)

Sigue estos pasos para lanzar todo el sistema en tu máquina local.

### 1. Iniciar la Infraestructura de Kafka

Este comando levantará en segundo plano un clúster de Kafka con 3 brokers, Zookeeper y una interfaz web de monitoreo.

```bash
docker-compose up -d
```

### 2. Iniciar la Aplicación Backend

Este comando compilará e iniciará el servidor de Spring Boot que contiene toda la lógica del backend (API, motor de juego, etc.). El servidor se ejecutará en el puerto `8081`.

```bash
mvn spring-boot:run
```
*Espera a que el log muestre el mensaje "Started KafkaWarsApplication..." antes de continuar.*

### 3. Ejecutar el Cliente de Prueba

En una **nueva terminal**, ejecuta este comando. Lanzará un cliente simple que enviará un comando de movimiento al backend.

```bash
mvn compile exec:java -Dexec.mainClass="com.kafkawars.client.KafkaWarsClient"
```
Verás en la salida del cliente la petición HTTP que se envía y en la terminal del backend verás los logs de cómo se procesa el comando a través de Kafka.

### 4. Monitorear Kafka (Opcional)

Puedes observar los tópicos, mensajes y consumidores de Kafka abriendo la interfaz de AKHQ en tu navegador:

**URL:** `http://localhost:8080`

---

## Pruebas en un Entorno Distribuido (Máquinas Virtuales)

¡Sí, es totalmente posible y una excelente idea! La arquitectura está diseñada para esto. Aquí tienes una guía conceptual:

1.  **Configuración de Red:** Asegúrate de que todas tus máquinas virtuales (VMs) estén en la misma red y puedan comunicarse entre ellas a través de sus direcciones IP.

2.  **Desplegar el Clúster de Kafka:** En lugar de usar Docker Compose, instalarías y ejecutarías un broker de Kafka en 3 VMs distintas.
    *   En el archivo de configuración de cada broker (`server.properties`), tendrías que ajustar la propiedad `advertised.listeners` para que apunte a la IP pública de su respectiva VM.

3.  **Desplegar el Backend:** En una 4ª VM, ejecutarías la aplicación Spring Boot (`mvn spring-boot:run`).
    *   En el archivo `application.properties`, cambiarías `spring.kafka.bootstrap-servers` de `localhost` a la lista de IPs de tus VMs de Kafka (ej: `ip_vm1:9092,ip_vm2:9092,ip_vm3:9092`).

4.  **Ejecutar el Cliente:** En una 5ª VM, ejecutarías el cliente.
    *   Necesitarías modificar el archivo `ClientConfig.java` para que la `API_BASE_URL` apunte a la IP de la VM del backend (ej: `http://ip_vm4:8081/api/v1`).

Este montaje simularía un entorno de producción real y te permitiría probar la resiliencia y escalabilidad del sistema.

## Estructura del Proyecto

*   `/api`: Contiene el `CommandController`, el punto de entrada (Ingress Gateway) para los comandos de los jugadores.
*   `/client`: El cliente interactivo de prueba.
*   `/config`: Clases de configuración de Spring y Kafka.
*   `/data`: Repositorio para el estado del juego (actualmente en memoria).
*   `/domain`: Los objetos de datos principales del juego (`MoveCommand`, `GameState`, etc.).
*   `/logic`: El `GameEngine` que contiene la lógica de negocio pura.
*   `/messaging`: Los productores y consumidores de Kafka que forman el "Bucle Kafka".
*   `/security`: El `SecurityValidator` para la validación de firmas de comandos.
