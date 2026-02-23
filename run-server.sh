#!/bin/bash

# Server script for KafkaWars
echo "--- Starting KafkaWars Server Infrastructure ---"
docker-compose up -d

# Get local IP (Robust method for Linux)
LOCAL_IP=$(hostname -I 2>/dev/null | awk '{print $1}')

# Fallback 1: Use 'ip route' (very reliable on modern Linux)
if [ -z "$LOCAL_IP" ]; then
    LOCAL_IP=$(ip route get 1.1.1.1 2>/dev/null | grep -oP 'src \K\S+')
fi

# Fallback 2: Generic 'ip addr' parsing
if [ -z "$LOCAL_IP" ]; then
    LOCAL_IP=$(ip addr show | grep 'inet ' | grep -v '127.0.0.1' | awk '{print $2}' | cut -d/ -f1 | head -n 1)
fi

# Fallback 3: If everything fails
if [ -z "$LOCAL_IP" ]; then
    LOCAL_IP="[NO SE PUDO DETECTAR TU IP AUTOMÁTICAMENTE]"
fi

echo "----------------------------------------------------"
echo " SERVER READY"
echo " Share this IP with other players: $LOCAL_IP"
echo "----------------------------------------------------"

echo "Starting Spring Boot Backend..."
mvn spring-boot:run
