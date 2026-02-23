#!/bin/bash

# Client script for KafkaWars
echo "--- Welcome to KafkaWars ---"
echo "Enter the IP address of the server (if it's this computer, just press Enter for 'localhost'):"
read -p "Server IP: " server_ip

if [ -z "$server_ip" ]; then
    server_ip="localhost"
fi

echo "Connecting to $server_ip..."

# Compile and run the client with the server IP as a system property
mvn compile exec:java -Dexec.mainClass="com.kafkawars.client.KafkaWarsClient" -Dserver.ip="$server_ip"
