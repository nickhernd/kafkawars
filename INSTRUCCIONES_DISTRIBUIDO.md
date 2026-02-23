# Guía de Juego Distribuido - KafkaWars

Este documento explica cómo ejecutar una partida de KafkaWars entre múltiples ordenadores en la misma red.

## 1. Preparación del Servidor (Host)

El ordenador que actúe como servidor debe ejecutar la infraestructura (Kafka) y el motor del juego.

1. Abre una terminal en la carpeta del proyecto.
2. Ejecuta el script de inicio:
   ```bash
   ./run-server.sh
   ```
3. **Importante:** El script mostrará una línea como esta:
   `Share this IP with other players: 192.168.1.XX`
   Anota esa dirección IP.

## 2. Unirse como Jugador (Clientes)

Cada jugador (incluido el que tiene el servidor si quiere jugar) debe seguir estos pasos:

1. Abre una terminal en la carpeta del proyecto.
2. Ejecuta el script de juego:
   ```bash
   ./play.sh
   ```
3. Cuando el programa lo solicite:
   * **Server IP:** Introduce la IP del servidor que anotaste en el paso anterior.
   * **Player ID:** Elige un nombre único (ej: `Alpha`, `Bravo`).

## 3. Cómo Jugar (Comandos)

Una vez dentro, el cliente se quedará esperando tus órdenes. El formato es:
`[id_unidad] [posición_x] [posición_y]`

### Ejemplos:
* Para mover tu primera unidad al centro: `mi_unidad 10 10`
* Para moverla un paso a la derecha: `mi_unidad 11 10`

### Reglas del Campo de Batalla:
* **Mapa:** El tablero es de **20x20** (coordenadas de 0 a 19).
* **Movimiento:** No puedes moverte más de **1.5 unidades** de distancia por turno (puedes moverte 1 casilla en horizontal, vertical o diagonal).
* **Colisiones:** No puedes moverte a una casilla que ya esté ocupada por otra unidad.

## 4. Monitoreo (Opcional)

Si quieres ver qué está pasando "bajo el capó" (los mensajes de Kafka), abre un navegador en el ordenador servidor y entra en:
`http://localhost:8080`

Aquí podrás ver los tópicos `game-commands` (órdenes recibidas) y `game-events` (acciones validadas y ejecutadas).
