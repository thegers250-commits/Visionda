@echo off
REM Script de compilación para Visualonda - Fase 0

setlocal enabledelayedexpansion

echo ============================================================
echo  COMPILACION VISUALONDA - FASE 0
echo ============================================================
echo.

REM Configurar Java 17
set JAVA_HOME=F:\java17-zulu\zulu17.50.19-ca-jdk17.0.11-win_x64
set PATH=!JAVA_HOME!\bin;%PATH%

REM Configurar Gradle
set GRADLE_HOME=F:\gradle\gradle-8.9
set PATH=!GRADLE_HOME!\bin;%PATH%

REM Verificar Java
echo [1/5] Verificando Java...
java -version 2>&1 | find "17.0" >nul
if errorlevel 1 (
    echo ERROR: Java 17 no encontrado en !JAVA_HOME!
    echo Descargalo de: https://www.azul.com/downloads/zulu/
    pause
    exit /b 1
)
echo OK - Java 17 detectado

REM Verificar Gradle
echo [2/5] Verificando Gradle...
if not exist "!GRADLE_HOME!\bin\gradle.bat" (
    echo ERROR: Gradle no encontrado en !GRADLE_HOME!
    echo Descargalo de: https://gradle.org/releases/
    pause
    exit /b 1
)
echo OK - Gradle 8.9 detectado

REM Limpiar
echo [3/5] Limpiando directorio...
if exist ".gradle" rmdir /s /q ".gradle" >nul 2>&1
if exist "build" rmdir /s /q "build" >nul 2>&1
if exist "app\build" rmdir /s /q "app\build" >nul 2>&1
echo OK - Directorio limpiado

REM Compilar
echo [4/5] Compilando proyecto...
echo.
call gradle clean build --no-daemon

REM Verificar resultado
echo.
echo [5/5] Verificando resultado...
if exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo.
    echo ============================================================
    echo  COMPILACION EXITOSA!
    echo ============================================================
    echo.
    echo Archivo generado:
    echo   app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo Siguientes pasos:
    echo   1. Conecta tu teléfono Android por USB
    echo   2. Ejecuta: adb install app\build\outputs\apk\debug\app-debug.apk
    echo   3. Abre la app "Visualonda" en tu teléfono
    echo.
    echo FASE 0 COMPLETADA!
    echo.
    pause
) else (
    echo.
    echo ============================================================
    echo  ERROR EN COMPILACION
    echo ============================================================
    echo.
    echo No se generó el APK
    echo Revisa los errores arriba
    echo.
    pause
    exit /b 1
)

endlocal
