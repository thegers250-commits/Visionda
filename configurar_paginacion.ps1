# Script para configurar paginación en Windows 11 Home
# EJECUTAR COMO ADMINISTRADOR

Write-Host "Configurando archivo de paginación..." -ForegroundColor Green

try {
    # Obtener configuración actual
    $pagefileSetting = Get-WmiObject -Query "SELECT * FROM Win32_PageFileSetting" -ErrorAction SilentlyContinue
    
    # Si existe, eliminar
    if ($pagefileSetting) {
        Write-Host "Eliminando configuración anterior..." -ForegroundColor Yellow
        $pagefileSetting.Delete()
    }
    
    # Crear nueva configuración
    Write-Host "Creando nueva configuración..." -ForegroundColor Yellow
    $pageFileClass = [WmiClass]"\\.\root\cimv2:Win32_PageFileSetting"
    $newPageFile = $pageFileClass.CreateInstance()
    $newPageFile.Name = "C:\pagefile.sys"
    $newPageFile.InitialSize = 4096
    $newPageFile.MaximumSize = 4096
    $newPageFile.Put()
    
    Write-Host "✅ Paginación configurada a 4 GB" -ForegroundColor Green
    Write-Host ""
    Write-Host "⚠️ IMPORTANTE: Debes reiniciar tu PC" -ForegroundColor Red
    Write-Host ""
    Write-Host "Reiniciando en 30 segundos..." -ForegroundColor Yellow
    Write-Host "Presiona CTRL+C para cancelar" -ForegroundColor Yellow
    
    Start-Sleep -Seconds 30
    
    Write-Host "Reiniciando..." -ForegroundColor Cyan
    Restart-Computer -Force
    
} catch {
    Write-Host "❌ Error: $_" -ForegroundColor Red
}
