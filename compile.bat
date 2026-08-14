@echo off
setlocal

echo Limpando build anterior...
if exist bin rmdir /s /q bin
mkdir bin

javac -encoding UTF-8 -d bin src\compilador\*.java
if errorlevel 1 (
    echo.
    echo ERRO na compilacao. Verifique se o JDK esta instalado e no PATH ^(rode: javac -version^).
    pause
    exit /b 1
)

echo Gerando JAR executavel...
jar cfe interface-equipe06.jar compilador.CompilerInterface -C bin .

echo.
echo Pronto! Para executar: java -jar interface-equipe06.jar
pause
