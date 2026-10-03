# Script agresivo para limpiar archivos bloqueados y reconstruir

Write-Host "=== Deteniendo TODOS los procesos de Gradle y Java ===" -ForegroundColor Red
Get-Process -Name "java" -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Get-Process -Name "gradle*" -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Get-Process -Name "kotlin*" -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 3

Write-Host "=== Eliminando manualmente archivos bloqueados ===" -ForegroundColor Yellow
$blockedPaths = @(
    "app\build\intermediates\incremental\packageDebug\tmp\debug\zip-cache",
    "app\build\intermediates\incremental\packageDebug\tmp\debug",
    "app\build\intermediates\incremental\packageDebug\tmp",
    "server\build\kotlin\compileKotlin\cacheable"
)

foreach ($path in $blockedPaths) {
    $fullPath = Join-Path $PSScriptRoot $path
    if (Test-Path $fullPath) {
        try {
            Remove-Item -Path $fullPath -Recurse -Force -ErrorAction Stop
            Write-Host "Eliminado: $path" -ForegroundColor Green
        } catch {
            Write-Host "No se pudo eliminar: $path (continuando...)" -ForegroundColor Yellow
        }
    }
}

Write-Host "=== Eliminando carpetas build completas ===" -ForegroundColor Yellow
$buildPaths = @("app\build", "server\build", "build")
foreach ($path in $buildPaths) {
    $fullPath = Join-Path $PSScriptRoot $path
    if (Test-Path $fullPath) {
        try {
            Remove-Item -Path $fullPath -Recurse -Force -ErrorAction Stop
            Write-Host "Eliminado: $path" -ForegroundColor Green
        } catch {
            Write-Host "No se pudo eliminar: $path (continuando...)" -ForegroundColor Yellow
        }
    }
}

Write-Host "=== Ejecutando gradle clean ===" -ForegroundColor Cyan
Set-Location $PSScriptRoot
.\gradlew.bat clean --no-daemon

Write-Host "=== Ejecutando gradle build ===" -ForegroundColor Cyan
.\gradlew.bat build --no-daemon

Write-Host "=== Build completado ===" -ForegroundColor Green
