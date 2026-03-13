# KafkaWars: The Event Sourced Battlefield

Juego distribuido multijugador con Apache Kafka como bus de eventos y Spring Boot como backend. Hasta 4 jugadores se enfrentan en un mapa 20×20 con combate en tiempo real vía WebSocket.

---

## Stack Tecnológico

| Capa | Tecnología |
|------|-----------|
| Lenguaje | Java 21 |
| Framework | Spring Boot 3.2 |
| Mensajería | Apache Kafka (3 brokers) |
| Tiempo real | WebSockets nativos |
| API | REST (HTTP) |
| Build | Apache Maven |
| Infraestructura | Docker Compose |

---

## Prerrequisitos

- **Java 21+**
- **Apache Maven**
- **Docker** y **Docker Compose**

---

## Cómo Ejecutar

### 1. Levantar Kafka

```bash
docker-compose up -d
```

Arranca un clúster de 3 brokers de Kafka en segundo plano. Puedes monitorizar los tópicos en **http://localhost:8080** (AKHQ).

### 2. Arrancar el backend

```bash
mvn spring-boot:run
```

El servidor queda escuchando en **http://localhost:8085**. Espera a ver en el log:

```
Started KafkaWarsApplication in X.XXX seconds
```

### 3. Abrir el juego

Abre **2 o más pestañas** (o navegadores distintos) en:

```
http://localhost:8085
```

Cada pestaña representa un jugador. Introduce tu nombre → **Join Battle** → espera a que haya al menos 2 jugadores → ¡a jugar!

---

## Controles

| Tecla | Acción |
|-------|--------|
| `W` | Mover arriba |
| `S` | Mover abajo |
| `A` | Mover izquierda |
| `D` | Mover derecha |
| `I` | Disparar arriba |
| `K` | Disparar abajo |
| `J` | Disparar izquierda |
| `L` | Disparar derecha |

---

## Reglas

- Cada unidad tiene **3 puntos de vida** (HP).
- Un disparo viaja en línea recta hasta impactar una unidad o salir del mapa.
- Al llegar a **0 HP**, la unidad es eliminada.
- **Gana** el último jugador vivo.
- Se pueden conectar hasta **4 jugadores** simultáneos.

---

## Arquitectura

```
[Browser] → WASD  → POST /api/v1/command (MOVE)
[Browser] → IJKL  → POST /api/v1/command (ATTACK)
               ↓
    [CommandController] → Kafka topic: game.commands
               ↓
    [GameLoopService] @KafkaListener
               ↓
    [GameEngine].processMove() / processAttack()
               ↓
    [GameStateRepository].save()
    [GameStateWebSocketHandler].broadcastState()
               ↓
    [Browser] recibe por WebSocket → renderiza canvas
```

### Estructura de paquetes

| Paquete | Contenido |
|---------|-----------|
| `api` | `CommandController` — endpoints REST + lobby |
| `config` | Configuración de Kafka y WebSocket |
| `data` | `GameStateRepository` — estado en memoria + lobby |
| `domain` | Records del juego: `GameState`, `UnitState`, `MoveCommand`, `AttackCommand`, etc. |
| `domain/events` | Eventos: `UnitMoved`, `UnitHit`, `ShotMissed`, `GameEnded` |
| `logic` | `GameEngine` — lógica pura sin efectos secundarios |
| `messaging` | `GameLoopService`, `CommandProducer`, `EventProducer` |
| `security` | `SecurityValidator` — validación de comandos |
| `websocket` | `GameStateWebSocketHandler` — push en tiempo real |

---

## API REST

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/v1/state/{matchId}` | Estado actual de la partida |
| `POST` | `/api/v1/command` | Enviar comando (MOVE o ATTACK) |
| `GET` | `/api/v1/lobby` | Estado del lobby actual |
| `POST` | `/api/v1/lobby/join` | Unirse al lobby `{ "playerId": "nombre" }` |

---

## Entorno distribuido (VMs)

Para desplegar en máquinas virtuales separadas:

1. **Kafka**: lanza un broker por VM, ajustando `advertised.listeners` con la IP pública de cada una.
2. **Backend**: en otra VM, modifica `application.properties`:
   ```properties
   spring.kafka.bootstrap-servers=ip_vm1:9092,ip_vm2:9092,ip_vm3:9092
   server.port=8085
   ```
3. **Clientes**: abrir el navegador apuntando a la IP del backend — `http://ip_backend:8085`.

---

## Tests

```bash
mvn test
```

9 tests unitarios sobre el dominio y el motor de juego, sin dependencias externas.


- [ ] decirle a claude que arregle los conflicot
