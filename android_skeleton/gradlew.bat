@echo off
REM Este script utiliza Gradle 8.7 para compilar

setlocal enabledelayedexpansion

set GRADLE_HOME=F:\gradle\gradle-8.7
if not exist "!GRADLE_HOME!" (
    echo Error: Gradle no encontrado en !GRADLE_HOME!
    echo Descargando Gradle 8.7...
    cd F:\
    powershell -Command "Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-8.7-bin.zip' -OutFile 'gradle-8.7.zip' ; Expand-Archive -Path 'gradle-8.7.zip' -DestinationPath 'gradle' -Force ; Remove-Item 'gradle-8.7.zip' -Force"
)

set PATH=!GRADLE_HOME!\bin;!PATH!
set JAVA_HOME=F:\java
set JAVA_TOOL_OPTIONS=-XX:+IgnoreUnrecognizedVMOptions --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED

call gradle.bat %*
