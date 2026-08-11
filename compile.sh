#!/bin/bash
# Compila o projeto e gera interface-equipeXX.jar (executável).
# Uso: ./compile.sh   (a partir da pasta raiz do projeto)

set -e

echo "Limpando build anterior..."
rm -rf bin
mkdir -p bin

echo "Compilando..."
javac -encoding UTF-8 -d bin src/compilador/*.java

echo "Gerando JAR executável..."
jar cfe interface-equipeXX.jar compilador.CompilerInterface -C bin .

echo "Pronto! Para executar: java -jar interface-equipeXX.jar"
