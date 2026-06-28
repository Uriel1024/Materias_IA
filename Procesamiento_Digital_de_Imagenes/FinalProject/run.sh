#!/bin/bash
# Script de compilacion y ejecucion del proyecto FinalProject
# Compila todas las clases y ejecuta el menu principal

cd "$(dirname "$0")"

echo "=== Compilando proyecto FinalProject ==="
mkdir -p out
find src -name "*.java" > /tmp/sources.txt
javac -d out @/tmp/sources.txt

if [ $? -eq 0 ]; then
    echo "Compilacion exitosa."
    echo "=== Ejecutando Menu Principal ==="
    java -cp out Main
else
    echo "Error en la compilacion."
    exit 1
fi
