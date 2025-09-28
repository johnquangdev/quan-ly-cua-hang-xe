#!/bin/bash

echo "Building the project..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "Maven build failed. Exiting."
    exit 1
fi

echo "Checking for .env file..."
if [ ! -f .env ]; then
    echo ".env file not found. Please create one based on example.env"
    echo "Exiting."
    exit 1
fi

echo "Starting the application..."
java -jar target/quan-ly-cua-hang-xe-1.0-SNAPSHOT.jar
