# Script para aumentar archivo de paginacion automaticamente
# REQUIERE: Ejecutar como ADMINISTRADOR

# Verificar si está ejecutando como admin
$isAdmin = ([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)

if (-not $isAdmin) {
    Write-Host "⚠️ REQUIERE PERMISOS DE ADMINISTRADOR" -ForegroundColor Red
    Write-Host ""
    Write-Host "Pasos:" -ForegroundColor Cyan
    Write-Host "1. Click DERECHO en PowerShell" -ForegroundColor White
    Write-Host "2. Selecciona 'Ejecutar como administrador'" -ForegroundColor White
    Write-Host "3. Ejecuta el script de nuevo" -ForegroundColor White
    exit 1
}

Write-Host "🔧 Aumentando archivo de paginación..." -ForegroundColor Cyan
Write-Host ""

# Obtener unidad del sistema (normalmente C:)
$systemDrive = $env:SystemDrive

Write-Host "Unidad del sistema: $systemDrive" -ForegroundColor Yellow

# Configurar nuevo tamaño (8 GB)
$initialSize = 8192  # MB
$maximumSize = 8192  # MB

Write-Host "Nuevo tamaño: $initialSize MB ~ $maximumSize MB" -ForegroundColor Yellow

# Usar WMI para configurar
try {
    Write-Host ""
    Write-Host "⏳ Configurando..." -ForegroundColor Cyan
    
    # Obtener instancia de paginación
    $pagefileSetting = Get-WmiObject -Query "SELECT * FROM Win32_PageFileSetting" -ErrorAction SilentlyContinue
    
    if ($pagefileSetting) {
        # Si existe, actualizar
        Write-Host "Actualizando configuración existente..." -ForegroundColor Gray
        $pagefileSetting.Delete()
    }
    
    # Crear nueva configuración
    $pageFileClass = [WmiClass]"\\.\root\cimv2:Win32_PageFileSetting"
    
    $newPageFile = $pageFileClass.CreateInstance()
    $newPageFile.Name = "$systemDrive\pagefile.sys"
    $newPageFile.InitialSize = $initialSize
    $newPageFile.MaximumSize = $maximumSize
    
    $newPageFile.Put()
    
    Write-Host "✅ Configuración aplicada" -ForegroundColor Green
    Write-Host ""
    
    # Mostrar nueva configuración
    Write-Host "📊 Nueva configuración:" -ForegroundColor Cyan
    Get-WmiObject -Query "SELECT * FROM Win32_PageFileSetting" | ForEach-Object {
        Write-Host "  Archivo: $($_.Name)" -ForegroundColor Yellow
        Write-Host "  Tamaño inicial: $($_.InitialSize) MB" -ForegroundColor Yellow
        Write-Host "  Tamaño máximo: $($_.MaximumSize) MB" -ForegroundColor Yellow
    }
    
    Write-Host ""
    Write-Host "⚠️ REQUIERE REINICIO" -ForegroundColor Red
    Write-Host ""
    Write-Host "El cambio se aplicará después de reiniciar tu PC" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "¿Deseas reiniciar ahora? (s/n)" -ForegroundColor Cyan
    $response = Read-Host
    
    if ($response -eq "s" -or $response -eq "S") {
        Write-Host ""
        Write-Host "Reiniciando en 10 segundos..." -ForegroundColor Yellow
        Start-Sleep -Seconds 10
        Restart-Computer -Force
    } else {
        Write-Host ""
        Write-Host "OK, reinicia manualmente cuando estés listo." -ForegroundColor Green
        Write-Host "Comando: Restart-Computer" -ForegroundColor Gray
    }
    
} catch {
    Write-Host "❌ Error: $_" -ForegroundColor Red
    Write-Host ""
    Write-Host "Alternativa: Aumenta manualmente en Panel de Control" -ForegroundColor Yellow
}
